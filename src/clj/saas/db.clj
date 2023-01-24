(ns saas.db
  "Namespace dedicated to DB setup and useful sql functions"
  (:require [hikari-cp.core :as hik]
            [integrant.core :as ig]
            [migratus.core :as migratus]
            [next.jdbc.connection :as conn]
            [next.jdbc :as jdbc]))

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

(defn db-initiated?
  "Check if the init script has been run"
  [{:keys [db migration-table-name]}]
  (let [sql [(str "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = '"
                  migration-table-name
                  "');")]]
    (:exists (jdbc/execute-one! db sql))))

;Initiate the db connection pool
(defmethod ig/init-key :saas/db
  [_ config]
  (println "Configuring db")
  (-> (config->jdbc-url config)
      (datasource)
      (jdbc/with-options jdbc/unqualified-snake-kebab-opts)))

(defmethod ig/halt-key! :saas/db
  [_ config]
  (hik/close-datasource (:connectable config)))


;Initiate migrations and run the init script if it hasn't been run yet
(defmethod ig/init-key :saas/migrator
  [_ config]
  (let [migratus-conf (merge config {:db {:datasource (-> config :db :connectable)}})]
    (println "Migrating db")
    (when-not (db-initiated? config)
      (println "Running init script")
      (migratus/init migratus-conf))
    (migratus/migrate migratus-conf)
    migratus-conf))