(ns saas.telegram.core
  "A Clojure library for interacting with the Telegram Bot API.
   - [Getting started](https://github.com/wdhowe/telegrambot-lib#usage)"
  (:gen-class)
  (:require [integrant.core :as ig]
            [saas.telegram.api.updates :as tbu]))

(defn create
  "Create a new Telegram Bot API instance.
   - No argument attempts to load the `bot-token` from the environment.
   - 1 argument will use the passed in `bot-token`."
  ([bot-token]
   {:bot-token bot-token}))

(defn telegram-webhook-url
  [bot-token webhook-url]
  (str
   "https://api.telegram.org/bot"
   bot-token
   "/setWebhook?url="
   webhook-url))


(defmethod ig/init-key :saas/telegram
  [_ config]
  (println "Initializing telegram client \n")
  (create (:nutri-bot-token config)))



(defmethod ig/init-key :saas/telegram-webhook
  [_ {:keys [telegram-config telegram]}]
  (let [{:keys [webhook-url]} telegram-config]
    (println "Initializing telegram webhook \n")
    (tbu/set-webhook telegram webhook-url)))

(defmethod ig/halt-key! :saas/telegram-webhook
  [_ {:keys [telegram]}]
  (tbu/set-webhook telegram ""))