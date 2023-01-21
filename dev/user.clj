(ns user
  (:require
    [clojure.java.io :as io]
    [integrant.core :as ig]
    [integrant.repl :as ig-repl]
    [integrant.repl.state :as state]
    [ring.mock.request :as mock]
    [muuntaja.core :as m]
    [saas.auth :as auth]
    [saas.config :refer [config]]))


(ig-repl/set-prep! config)

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(defn app [] (-> state/system :designvote/app))
(defn db [] (-> state/system :db/postgres))
(defn auth [] (-> state/system :auth/cognito))



(comment

  (io/file "resources")

  (auth/create-cognito-account (auth) {:email "thisistest@example.com" :password "Pa$$w0rd"})

  )