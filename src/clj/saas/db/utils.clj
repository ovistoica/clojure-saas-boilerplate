(ns saas.db.utils
  (:require [clojure.tools.logging :as log]
            [honey.sql :as sql]
            [clojure.string :as string]
            [next.jdbc :as jdbc])
  (:import (java.sql SQLException SQLRecoverableException)))

(defn query->str [query]
  (let [[sql-string & params] (sql/format query)]
    (reduce (fn [sql-query param]
              (string/replace-first
               sql-query "?"
               (cond
                 (string? param) (str "'" param "'")
                 :else param)))
            sql-string params)))

(defn log-sql-query [query]
  (log/info (query->str query))
  query)


(defn execute-one!
  "Calls next.jdbc/execute-one! with a honeysql query and ensures to escape mysql
  properly. Ensures compatibility with clojure.java.jdbc query results."
  ([db query]
   (execute-one! db query {}))
  ([db query opts]
   (letfn [(exec-one! []
             (when (:debug opts)
               (log-sql-query query))
             (jdbc/execute-one!
              db (sql/format query)
              (merge
               jdbc/unqualified-snake-kebab-opts
               (dissoc opts :debug))))]
     (try (exec-one!)
          (catch SQLRecoverableException _
            (exec-one!))
          (catch SQLException e
            (log/info query)
            (throw e))))))

(defn execute!
  "Calls next.jdbc/execute! with a honeysql query and ensures to escape mysql
  properly. Ensures compatibility with clojure.java.jdbc query results."
  ([db query]
   (execute! db query {}))
  ([db query opts]
   (letfn [(exec! []
             (when (:debug opts)
               (log-sql-query query))
             (next.jdbc/execute!
              db
              (sql/format query)
              (merge
               jdbc/unqualified-snake-kebab-opts
               (dissoc opts :debug))))]
     (try (exec!)
          (catch SQLRecoverableException _
            (exec!))
          (catch SQLException e
            (log-sql-query query)
            (throw e))))))

(defn upsert!
  "Does a INSERT ... ON DUPLICATE KEY UPDATE"
  [connectable table key-map]
  (execute-one! connectable
                {:insert-into table
                 :columns (keys key-map)
                 :values [(vals key-map)]
                 :on-duplicate-key-update key-map}
                {:return-keys true}))
