(ns saas.server
  (:require
    [ring.adapter.jetty :as jetty]
    [integrant.core :as ig]
    [saas.router :as router]
    [saas.config :as c])
  (:import
    (org.eclipse.jetty.server Server)))



(defn app
  [config]
  (router/routes config))


(defmethod ig/init-key :server/jetty
  [_ {:keys [handler port]}]
  (println (str "\nServer running on port " port))
  (jetty/run-jetty handler {:port port :join? false}))

(defmethod ig/init-key :webserver/port
  [_ config]
  config)

(defmethod ig/init-key :db/migrations
  [_ config]
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
  (let [config (dissoc (c/config) :saas/secrets)]
    (-> config ig/prep ig/init)))

