(ns saas.util
  (:require [honey.sql :as sql]
            [clojure.string :as string]
            [clojure.tools.logging :as log]
            [clojure.walk :refer [postwalk]]
            [next.jdbc :as jdbc])
  (:import (java.sql SQLException SQLRecoverableException)))

(defn query->str
  "Converts a honeysql query to an SQL string."
  [query]
  (let [[sql-string & params] (sql/format query :quoting :postgresql)]
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

(defn execute!
  "Calls next.jdbc/execute! with a honeysql query.
   Ensures compatibility with clojure.java.jdbc query results."
  ([db query]
   (execute! db query {}))
  ([db query opts]
   (letfn [(exec! []
             (when (:debug opts)
               (log-sql-query query))
             (jdbc/execute!
               db
               (sql/format query)
               (dissoc opts :debug)))]
     (try (exec!)
          (catch SQLRecoverableException _
            (exec!))
          (catch SQLException e
            (log-sql-query query)
            (throw e))))))

(defn snake->kebab
  "Transforms snake_case input to kebab-case. Always outputs strings"
  [x]
  (-> x name (string/replace "_" "-")))

(defn kebab->snake
  "Transforms kebab-case input to snake_case. Always outputs strings"
  [x]
  (-> x name (string/replace "-" "_")))

(defn- camel->* [sep]
  (let [conv (into {}
                   (map #(vector %
                                 (->> % string/lower-case (str sep))))
                   "ABCDEFGHIJKLMNOPQRSTUVWXYZ")]
    (fn camel->* [x]
      (->> x name (replace conv) (apply str)))))

(def camel->kebab
  "Transforms camelCase input to kebab-case. Always outputs strings"
  (camel->* "-"))

(def camel->snake
  "Transforms camelCase input to snake_case. Always outputs strings"
  (camel->* "_"))

(defn ->camel
  "Transforms kebab-case or snake_case input to camelCase. Always outputs strings"
  [x]
  (let [[first & rest] (-> x name (string/split #"_|-"))]
    (apply str first (map string/capitalize rest))))

(def snake->kebab-keyword (comp keyword snake->kebab))
(def kebab->snake-keyword (comp keyword kebab->snake))

(defn transform-keys
  "Recursively transforms all map keys in coll with t."
  [t coll]
  (letfn [(transform [[k v]] [(t k) v])]
    (postwalk (fn [x] (if (map? x) (into {} (map transform) x) x)) coll)))

(def kebab-keys (partial transform-keys snake->kebab-keyword))
(def snake_keys (partial transform-keys kebab->snake-keyword))
(def camelKeys (partial transform-keys ->camel))