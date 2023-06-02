(ns playground
  (:require [clojure.edn :as edn]
            [integrant.repl.state :as state]
            [saas.telegram.api.updates :as tbu]
            [saas.openai.api :as openai]))

(defn telegram [] (-> state/system :saas/telegram))
(defn db [] (-> state/system :saas/db))
(defn openai [] (-> state/system :saas/openai))


(comment
 (tbu/get-updates (telegram))

 (tbu/delete-webhook (telegram))
 (tbu/get-webhook-info (telegram)))


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

 (tbot/send-message (telegram) {:chat_id ovi-chat-id
                                :text recipe-response})


 (tbot/send-message (telegram) {:chat_id isa-chat-id
                                :text first-meal-response})

 (tbot/send-message (telegram) {:chat_id isa-chat-id
                                :text totals-for-today})

 )

