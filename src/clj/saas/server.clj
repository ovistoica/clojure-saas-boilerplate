(ns saas.server
  (:require
    [aero.core :as a]
    [clojure.java.io :as io]
    [ring.adapter.jetty :as jetty]
    [integrant.core :as ig]
    [saas.router :as router])
  (:import
    (org.eclipse.jetty.server Server)))

(defmethod aero.core/reader 'ig/ref
  [{:keys [profile] :as opts} tag value]
  (ig/ref value))

(defn config
  []
  (a/read-config (io/resource "config.edn")))


(defn webserver-port
  []
  (Long/parseLong (str (get (config) :webserver/port))))

(defn app
  [config]
  (router/routes config))

(defmethod ig/init-key :server/jetty
  [_ {:keys [handler port]}]
  (println (str "\nServer running on port " port))
  (jetty/run-jetty handler {:port port :join? false}))

(defmethod ig/init-key :webserver/port
  [_ config]
  (println "\nConfigured port")
  config)

(defmethod ig/init-key :saas/handler
  [_ config]
  (println "\nStarted app")
  (app config))


(defmethod ig/halt-key! :server/jetty
  [_ ^Server server]
  (.stop server))

(defn -main
  []
  (let [config (config)]
    (-> config ig/prep ig/init)))

