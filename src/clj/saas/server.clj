(ns saas.server
  (:require
   [ring.adapter.jetty :as jetty]
   [integrant.core :as ig]
   [saas.router :as router]
   [saas.openai.openai]
   [saas.telegram.core]
   [saas.db.core]
   [nrepl.cmdline :as nrepl-cmd]
   [saas.config :as c])
  (:import
   (org.eclipse.jetty.server Server))
  (:gen-class))



(defn app
  [config]
  (router/routes config))

(defmethod ig/init-key :server/jetty
  [_ {:keys [handler port]}]
  (println (str "Server running on port " port "\n"))
  (jetty/run-jetty handler {:port port :join? false}))

(defmethod ig/init-key :webserver/port
  [_ config]
  config)

(defmethod ig/init-key :db/migrations
  [_ config]
  config)

(defmethod ig/init-key :saas/handler
  [_ config]
  (println "Started app \n")
  (app config))

(defmethod ig/init-key :saas/environment
  [_ config]
  config)

(defmethod ig/halt-key! :server/jetty
  [_ ^Server server]
  (.stop server))


(defn start-system
  []
  (let [config (dissoc (c/config) :saas/secrets)]
    (-> config (ig/prep) (ig/init))))

(defn -main
  [& args]
  (start-system)
  (apply nrepl-cmd/-main args))

