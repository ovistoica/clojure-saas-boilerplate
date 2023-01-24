(ns user
  (:require
    [clojure.java.io :as io]
    [integrant.core :as ig]
    [integrant.repl :as ig-repl]
    [integrant.repl.state :as state]
    [ring.mock.request :as mock]
    [muuntaja.core :as m]
    [saas.auth :as auth]
    [migratus.core :as mi]
    [saas.config :refer [config]]
    [saas.db :as db]))


(ig-repl/set-prep! config)

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(defn app [] (-> state/system :saas/handler))
(defn db [] (-> state/system :saas/db))
(defn migration-config [] (-> state/system :saas/migrator))
(defn auth [] (-> state/system :auth/cognito))



(comment

  (io/file "resources")

  (auth/create-cognito-account (auth) {:email "thisistest@example.com" :password "Pa$$w0rd"})

  )