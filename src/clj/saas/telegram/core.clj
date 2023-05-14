(ns saas.telegram.core
  "A Clojure library for interacting with the Telegram Bot API.
   - [Getting started](https://github.com/wdhowe/telegrambot-lib#usage)"
  (:gen-class)
  (:require [integrant.core :as ig]))

(defn create
  "Create a new Telegram Bot API instance.
   - No argument attempts to load the `bot-token` from the environment.
   - 1 argument will use the passed in `bot-token`."
  ([bot-token]
   {:bot-token bot-token}))


(defmethod ig/init-key :saas/telegram
  [_ config]
  (println "Initializing telegram client \n")
  (create (:nutri-bot-token config)))


