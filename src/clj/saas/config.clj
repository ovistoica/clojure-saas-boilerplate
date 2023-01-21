(ns saas.config
  (:require
    [clojure.java.io :as io]
    [integrant.core :as ig]
    [aero.core :as a]))

(defmethod aero.core/reader 'ig/ref
  [{:keys [profile] :as opts} tag value]
  (ig/ref value))

(defn config
  []
  (a/read-config (io/resource "config.edn")))


(defn webserver-port
  []
  (Long/parseLong (str (get (config) :webserver/port))))

(defn migrations-config
  []
  (get (config) :db/migrations))

(defmethod ig/init-key :saas/secrets
  [_ config]
  config)
