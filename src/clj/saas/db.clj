(ns saas.db
  "Namespace dedicated to DB setup and useful sql functions"
  (:require [hikari-cp.core :as hik]
            [integrant.core :as ig]
            [next.jdbc.connection :as conn]
            [next.jdbc :as jdbc]
            [saas.config :as c]))

(defn config->jdbc-url
  [config]
  (if (:jdbc-url config)
    (:jdbc-url config)
    (conn/jdbc-url config)))

(defn datasource
  "Create the hikari-cp datasource. Hikari is a very fast production ready JDBC connection pool.
  See https://github.com/brettwooldridge/HikariCP for more info"
  ([jdbc-url]
   (datasource jdbc-url 8))
  ([jdbc-url max-pool-size]
   (hik/make-datasource {:jdbc-url jdbc-url
                         :maximum-pool-size max-pool-size})))

(defmethod ig/init-key :saas/db
  [_ config]
  (-> (config->jdbc-url config)
      (datasource)
      (jdbc/with-options jdbc/unqualified-snake-kebab-opts)))

(defmethod ig/halt-key! :saas/db
  [_ config]
  (hik/close-datasource (:connectable config)))



(comment

  (config->jdbc-url (:saas/db (c/config)))

  (def jdbc-url (conn/jdbc-url (:saas/db (c/config))))

  (datasource jdbc-url)

  )