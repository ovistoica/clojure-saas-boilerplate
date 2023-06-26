(ns saas.telegram.handlers
  (:require [integrant.core :as ig]
            [integrant.repl.state :as state]
            [clojure.edn :as edn]
            [clojure.core.async :refer [chan <! go thread close! go-loop >!]]
            [saas.telegram.api.methods :as tbot]
            [saas.telegram.api.updates :as tbu]
            [clojure.tools.logging :as log]
            [saas.openai.prompts :as prompts]
            [saas.openai.api :as openai]
            [saas.telegram.db :as db]
            [saas.util :as u]))

(def initial-response
  "Hello!
I'm SnackTalk, your personal AI-powered nutrition assistant.
I'm here to make tracking your meals and understanding your nutrition as simple as possible.
Let's get started!
What did you have for your last meal?")

(defn macros->resp
  [{:keys [calories protein proteins carbs fat]}]
  (str "Calories: " calories "\n"
       "Protein: " (or protein proteins) "\n"
       "Carbs: " carbs "\n"
       "Fat: " fat "\n"))

(defn openai-response
  [openai prompt]
  (log/info "Prompt: " prompt)
  (let [response (->> (openai/create-chat-completion
                       openai
                       {:model "gpt-3.5-turbo"
                        :messages [{:role "user"
                                    :content prompt}]})
                      :choices
                      first
                      :message
                      :content)]
    (log/info "Ai Response: " response)
    response))

(defn openai-calorie-response
  [openai prompt]
  (-> (openai-response openai prompt)
      (edn/read-string)))

(defn prompt->calorie-input
  [{:keys [calories protein carbs fat text user-id date]}]
  {:calories calories
   :log-time (u/unix-timestamp->java-timestamp date)
   :proteins protein
   :carbs carbs
   :fat fat
   :food-input text
   :user-id user-id})


(defn handle-calorie-message
  [{:keys [chat-id text user-id date]} {:keys [telegram openai db]}]
  (let [prompt (prompts/calories-and-macros text)
        macros (openai-calorie-response openai prompt)
        response (macros->resp macros)
        calorie-log (prompt->calorie-input
                     (assoc macros :text text :user-id user-id :date date))]
    (db/insert-calorie-log! db calorie-log)
    (tbot/send-message telegram {:chat_id chat-id :text response})))

(defn bot-command
  [message]
  (let [text (-> message :text)
        entities (-> message :entities)]
    (when-let [command-entity (->> entities
                                   (filter #(= (:type %) "bot_command"))
                                   first)]
      (subs text (:offset command-entity) (:length command-entity)))))

(defn group-chat?
  [message]
  (-> message :chat :type (= "group")))

(defn user
  [{:keys [from chat] :as message}]
  (-> (if (group-chat? message) from (merge from chat))
      (select-keys
       [:id
        :is_bot
        :first_name
        :last_name
        :username
        :language_code])))

(def user-id 1641544258)

(defn total-calories-for-today
  [db user-id]
  (let [calorie-entries (db/select-today-calorie-entries db user-id)]
    (-> (reduce (fn [acc calorie-log]
                  (-> acc
                      (update :calories + (:calories calorie-log))
                      (update :proteins + (:proteins calorie-log))
                      (update :carbs + (:carbs calorie-log))))
                {}
                calorie-entries)
        (select-keys [:calories :proteins :carbs :fat]))))

(defn recommend-food
  [user-id config]
  (let [calories (->> (total-calories-for-today (:db config) user-id)
                      :calories)
        _ (println calories)
        prompt (str "Pretend you are a nutrition food recomandation giving bot. Give me food recommendations for my next meal considering I already ate "
                    calories
                    " calories today. "
                    "The recommendations should be healthy and low in calories for a normal human being.
                    Reply with only the recommendations or if my calories are over the recommended amount, reply with a message saying you ate enough for today")
        response (openai-response (:openai config) prompt)]
    response))


(defn handle-telegram-message
  [{:keys [db telegram] :as config} message]
  (let [user (user message)
        chat-id (-> message :chat :id)
        text (-> message :text)
        date (-> message :date)
        bot-command (bot-command message)]
    (when-not (db/get-telegram-user db (:id user))
      (db/insert-telegram-user! db user))
    (cond
      (= bot-command "/start") (tbot/send-message
                                telegram
                                {:chat_id chat-id
                                 :text initial-response})
      (= bot-command "/recommend") (tbot/send-message
                                    telegram
                                    {:chat_id chat-id
                                     :text (recommend-food (:id user) config)})
      (= bot-command "/today") (tbot/send-message
                                telegram
                                {:chat_id chat-id
                                 :text (-> (total-calories-for-today db (:id user))
                                           (macros->resp))})
      :else (handle-calorie-message
             {:chat-id chat-id
              :text text
              :date date
              :user-id (:id user)}
             config))))

(defn respond-ok
  []
  {:body "ok", :status 200})

(defn telegram-webhook-handler
  [{:keys [db] :as config}]
  (fn [req]
    (log/info "Telegram Webhook Request Body: " (-> req :parameters :body))
    (let [message (-> req :parameters :body :message)]
      (if (or (group-chat? message) (nil? message))
        (respond-ok)
        (go (>!  )
            (respond-ok))))))


;; This is the channel where the telegram
;; webhook puts messages, and the telegram handler
;; takes them from in order to respond to clients
(defmethod ig/init-key :telegram/channel
  [_ _]
  (chan 1024))


(defmethod ig/init-key :telegram/threads
  [_ {:keys [telegram-channel] :as config}]
  ;; Threads that handle telegram messages
  (for [_ (range 8)]
    (go-loop []
      (when-some [message (<! telegram-channel)]
        (<! (thread (handle-telegram-message config message)))
        (recur)))))

(defmethod ig/halt-key! :telegram/channel
  [_ chan]
  (close! chan))

(defmethod ig/halt-key! :telegram/threads
  [_ thread-chans]
  (doseq [c (or thread-chans [])]
    (close! c)))


(comment
 (defn telegram [] (-> state/system :saas/telegram))
 (defn db [] (-> state/system :saas/db))
 (defn openai [] (-> state/system :saas/openai))

 (openai-calorie-response (openai)
                          (prompts/new-calorie-prompt "1 medium pepperoni pizza"))

 (openai/create-chat-completion
  (openai)
  {:model "gpt-3.5-turbo"
   :messages [{:role "user"
               :content
               (prompts/new-calorie-prompt "1 medium pepperoni pizza")
               }]})
 (tbu/get-updates (telegram))
 (tbu/set-webhook (telegram) ""))
