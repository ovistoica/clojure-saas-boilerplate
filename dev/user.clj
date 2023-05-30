(ns user
  (:require
   [clojure.java.io :as io]
   [integrant.repl :as ig-repl]
   [integrant.repl.state :as state]
   [next.jdbc :as jdbc]
   [saas.auth :as auth]
   [saas.telegram.api.methods :as tbot]
   [saas.telegram.api.updates :as tbu]
   [saas.config :refer [config]]
   [saas.router :as router]
   [saas.db.core :as db]))


(ig-repl/set-prep! config)

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)


(defn app [] (-> state/system :saas/handler))
(defn db [] (-> state/system :saas/db))
(defn migration-config [] (-> state/system :saas/migrator))
(defn auth [] (-> state/system :auth/cognito))
(defn openai [] (-> state/system :saas/openai))
(defn telegram [] (-> state/system :saas/telegram))

(defn message->text
  [message]
  (-> message :message :text))


(comment
 (openai)

 (tbot/get-me (telegram))
 (tbot/get-up)
 (tbot/send-message (telegram) {:text "Hello"})
 (->> (tbu/get-updates (telegram))
      :result
      (map message->text)
      )

 (reset)


 ((app) {:request-method :post :uri "/v1/account/sign-up"})

 (router/routes (app))


 (db/db-initiated? (merge (migration-config) {:db (db)}))

 (io/file "resources")
 (io/file "resources/openai-openapi.yml")

 (auth/create-cognito-account (auth) {:email "thisistest@example.com" :password "Pa$$w0rd"})

 )

(comment


 (let [sql ["select tablename from pg_tables where schemaname = 'public';"]
       tables (->> (jdbc/execute! (db) sql)
                   (map :tablename)
                   (set))]
   (= tables)))

