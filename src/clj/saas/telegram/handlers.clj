(ns saas.telegram.handlers
  (:require [integrant.repl.state :as state]
            [clojure.edn :as edn]
            [saas.telegram.api.methods :as tbot]
            [clojure.tools.logging :as log]
            [saas.telegram.api.updates :as tbu]
            [saas.openai.prompts :as prompts]
            [saas.openai.api :as openai]
            [saas.telegram.db :as db]))

(defn calories->resp
  [{:keys [calories protein carbs fat]}]
  (str "Calories: " calories "\n"
       "Protein: " protein "\n"
       "Carbs: " carbs "\n"
       "Fat: " fat "\n"))

(defn openai-response
  [openai prompt]
  (->> (openai/create-chat-completion
        openai
        {:model "gpt-3.5-turbo"
         :messages [{:role "user"
                     :content prompt}]})
       :choices
       first
       :message
       :content
       (edn/read-string)
       (calories->resp)))

(defn handle-message
  [{:keys [message]} {:keys [telegram openai]}]
  (let [chat-id (-> message :chat :id)
        text (-> message :text)
        prompt (prompts/calories-and-macros text)
        response (openai-response openai prompt)]
    (log/info "Handling message" message response)
    (tbot/send-message telegram {:chat_id chat-id :text response})))


(defn handle-chat-messages
  [chat-id messages {:keys [db] :as config}]
  (let [latest-update-id (or (db/get-latest-update-id db chat-id)
                             (->> (map :update_id messages)
                                  (apply min)
                                  (dec)))
        new-messages (->> messages
                          (filter #(> (:update_id %) latest-update-id)))]
    (doseq [message new-messages]
      (handle-message message config))
    (when-let [update-id (some->> (seq new-messages)
                                  (map :update_id)
                                  (apply max))]
      (db/upsert-chat-last-update-id! db {:update-id update-id
                                          :chat-id chat-id}))))


(defn check-for-updates
  [db telegram]
  (let [updates (->> (tbu/get-updates telegram)
                     :result)
        updates-by-keys (group-by #(-> % :message :chat :id) updates)
        chat-ids (keys updates-by-keys)]
    (doseq [chat-id chat-ids]
      (handle-chat-messages db chat-id (get updates-by-keys chat-id)))))


(defn telegram [] (-> state/system :saas/telegram))
(defn db [] (-> state/system :saas/db))
(defn openai [] (-> state/system :saas/openai))

(defn telegram-webhook-handler
  [config]
  (fn [req]
    (let [message-body (-> req :parameters :body)]
      (handle-message message-body config)))
  )



(comment
 (tbu/get-updates (telegram))
 (tbu/set-webhook (telegram) url)
 (check-for-updates (db) (telegram))

 "100 grams of whole wheat spaghetti \\\\n100 grams of shrimp \\\\n100 grams of veggie mix (zucchini, bell pepper, carrot) \\\\n20 grams of butter \\\\n10 grams of nutritional yeast"
 )