(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.core :as ig]
            [integrant.repl.state :as state]
            [saas.server :refer [config]]
            [clojure.java.io :as io]
            [ring.mock.request :as mock]
            [muuntaja.core :as m]))


(ig-repl/set-prep! config)

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(def app (-> state/system :designvote/app))
(def db (-> state/system :db/postgres))
