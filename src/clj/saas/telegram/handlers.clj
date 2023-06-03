(ns saas.telegram.handlers
  (:require [integrant.repl.state :as state]
            [clojure.edn :as edn]
            [saas.telegram.api.methods :as tbot]
            [saas.telegram.api.updates :as tbu]
            [saas.openai.prompts :as prompts]
            [saas.openai.api :as openai]
            [saas.telegram.db :as db]
            [saas.util :as u]
            [tripod.log :as log]))

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
    (tbot/send-message telegram {:chat_id chat-id :text response})
    {:body "ok", :status 200}))

(defn bot-command
  [message]
  (let [text (-> message :text)
        entities (-> message :entities)]
    (when-let [command-entity (->> entities
                                   (filter #(= (:type %) "bot_command"))
                                   first)]
      (subs text (:offset command-entity) (:length command-entity)))))

(defn user
  [{:keys [from chat]}]
  (-> (merge from chat)
      (select-keys
       [:id
        :is_bot
        :first_name
        :last_name
        :username
        :language_code])))

(defn total-calories-for-today
  [db user-id]
  (-> (->> (db/select-today-calorie-entries db user-id)
           (reduce (fn [acc calorie-log]
                     (-> acc
                         (update :calories + (:calories calorie-log))
                         (update :proteins + (:proteins calorie-log))
                         (update :carbs + (:carbs calorie-log))))))
      (select-keys [:calories :proteins :carbs :fat])))

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


(defn telegram-webhook-handler
  [{:keys [db] :as config}]
  (fn [req]
    (let [message (-> req :parameters :body :message)
          chat-id (-> message :chat :id)
          text (-> message :text)
          date (-> message :date)
          bot-command (bot-command message)
          user (user message)]
      (try
        (when-not (db/get-telegram-user db (:id user))
          (db/insert-telegram-user! db user))
        (cond
          (= bot-command "/start")
          (do (tbot/send-message (:telegram config) {:chat_id chat-id :text initial-response})
              {:body "ok", :status 200})
          (= bot-command "/recommend")
          (do
            (tbot/send-message (:telegram config)
                               {:chat_id chat-id
                                :text (recommend-food (:id user) config)})
            {:body "ok", :status 200})
          (= bot-command "/today")
          (do (tbot/send-message (:telegram config)
                                 {:chat_id chat-id
                                  :text (macros->resp (total-calories-for-today db (:id user)))})
              {:body "ok", :status 200})
          :else
          (handle-calorie-message
           {:chat-id chat-id
            :text text
            :date date
            :user-id (:id user)}
           config))
        (catch Exception e
          (log/trace e)
          {:body "error", :status 500})))))


(comment
 (def req {:parameters {:body {:message {:chat {:first_name "Ovidiu",
                                                :id 1641544258,
                                                :last_name "Stoica",
                                                :type "private",
                                                :username "ovistoica"},
                                         :date 1685769972,
                                         :from {:first_name "Ovidiu",
                                                :id 1641544258,
                                                :is_bot false,
                                                :language_code "en",
                                                :last_name "Stoica",
                                                :username "ovistoica"},
                                         :message_id 315,
                                         :text "test"},
                               :update_id 714250907}}})

 (def req-start {:parameters {:body {:message {:chat {:first_name "Ovidiu",
                                                      :id 1641544258,
                                                      :last_name "Stoica",
                                                      :type "private",
                                                      :username "ovistoica"},
                                               :date 1685770046,
                                               :entities [{:length 6,
                                                           :offset 0,
                                                           :type "bot_command"}],
                                               :from {:first_name "Ovidiu",
                                                      :id 1641544258,
                                                      :is_bot false,
                                                      :language_code "en",
                                                      :last_name "Stoica",
                                                      :username "ovistoica"},
                                               :message_id 317,
                                               :text "/start"},
                                     :update_id 714250908}}})

 (bot-command (-> req-start :parameters :body :message))
 (user (-> req-start :parameters :body :message))

 )




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
 (tbu/set-webhook (telegram) "")


 "100 grams of whole wheat spaghetti \\\\n100 grams of shrimp \\\\n100 grams of veggie mix (zucchini, bell pepper, carrot) \\\\n20 grams of butter \\\\n10 grams of nutritional yeast"
 )
