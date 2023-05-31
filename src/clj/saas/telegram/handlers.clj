(ns saas.telegram.handlers
  (:require [integrant.repl.state :as state]
            [clojure.edn :as edn]
            [saas.telegram.api.methods :as tbot]
            [saas.telegram.api.updates :as tbu]
            [saas.openai.prompts :as prompts]
            [saas.openai.api :as openai]
            [saas.telegram.db :as db]))

(defn telegram [] (-> state/system :saas/telegram))
(defn db [] (-> state/system :saas/db))
(defn openai [] (-> state/system :saas/openai))

(defn calories->resp
  [{:keys [calories protein carbs fat]}]
  (str "Calories: " calories "\n"
       "Protein: " protein "\n"
       "Carbs: " carbs "\n"
       "Fat: " fat "\n"))

(defn openai-response
  [prompt]
  (->> (openai/create-chat-completion (openai) {:model "gpt-3.5-turbo"
                                                :messages [{:role "user"
                                                            :content prompt}]})
       :choices
       first
       :message
       :content
       (edn/read-string)
       (calories->resp)))


(def isa-chat-id 5786497568)
(def ovi-chat-id 1641544258)
(def greeting "Hello! I'm SnackTalk, your personal AI-powered nutrition assistant. I'm here to make tracking your meals and understanding your nutrition as simple as possible. Let's get started! What did you have for your last meal?")
(def first-meal-response
  "Calories: 333
Protein: 35.7g
Carbs: 10.2g
Fat: 15.6g")
(def recipe-response
  "Sure! Here's a simple Lemon Garlic Salmon with Asparagus recipe:\n\nIngredients:\n\n200g Salmon fillet\n100g Asparagus\n1 tbsp Olive oil\n2 Garlic cloves\nHalf a Lemon\nInstructions:\n\nPreheat oven to 200°C.\nPlace salmon on foil, drizzle with oil, squeeze lemon, add minced garlic, salt, and pepper.\nArrange asparagus around salmon, fold foil into a packet.\nBake 15-20 minutes.\nServe & enjoy!\nThis should be approximately under 650 calories.")

(def second-meal-response
  "Calories: 630
Protein: 50.4g
Carbs: 8.8g
Fat: 42.9g")


(def totals-for-today
  "Totals for today:

Calories: 963
Protein: 86.1g
Carbs: 19g
Fat: 58.5g")


(comment

 (tbot/send-message (telegram) {:chat_id isa-chat-id
                                :text greeting})

 (tbot/send-message (telegram) {:chat_id isa-chat-id
                                :text second-meal-response})

 (tbot/send-message (telegram) {:chat_id isa-chat-id
                                :text recipe-response})


 (tbot/send-message (telegram) {:chat_id isa-chat-id
                                :text first-meal-response})

 (tbot/send-message (telegram) {:chat_id isa-chat-id
                                :text totals-for-today})

 )


(defn handle-message
  [{:keys [message]}]
  (let [chat-id (-> message :chat :id)
        text (-> message :text)
        prompt (prompts/calories-and-macros text)
        response (openai-response prompt)]
    (tbot/send-message (telegram) {:chat_id chat-id :text response})))


(defn handle-chat-messages
  [db chat-id messages]
  (let [latest-update-id (or (db/get-latest-update-id db chat-id)
                             (->> (map :update_id messages)
                                  (apply min)
                                  (dec)))
        new-messages (->> messages
                          (filter #(> (:update_id %) latest-update-id)))]
    (doseq [message new-messages]
      (handle-message message))
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

(comment
 (check-for-updates (db) (telegram))

 "100 grams of whole wheat spaghetti \\\\n100 grams of shrimp \\\\n100 grams of veggie mix (zucchini, bell pepper, carrot) \\\\n20 grams of butter \\\\n10 grams of nutritional yeast"
 )