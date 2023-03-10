(ns saas.middleware
  (:require
    [reitit.ring.middleware.exception :as exception]
    [clojure.pprint :as pp]
    [saas.util :as u])
  (:import (java.sql SQLException)))

;; type hierarchy
(derive ::error ::exception)
(derive ::failure ::exception)
(derive ::horror ::exception)

(defn handler [message exception request]
  (pp/pprint exception)
  {:status 500
   :body {:message message
          :exception (.getClass exception)
          :data (ex-data exception)
          :uri (:uri request)}})

(def exception-middleware
  (exception/create-exception-middleware
    (merge
      exception/default-handlers
      {;; ex-data with :type ::error
       ::error (partial handler "error")

       ;; ex-data with ::exception or ::failure
       ::exception (partial handler "exception")

       ;; SQLException and all it's child classes
       SQLException (partial handler "sql-exception")

       ;; override the default handler
       ::exception/default (partial handler "default")

       ;; print stack-traces for all exceptions
       ::exception/wrap (fn [handler e request]
                          (println "ERROR" (pr-str (:uri request)))
                          (clojure.pprint/pprint e)
                          (handler e request))})))


(def wrap-snake->kebab->snake
  {:name ::case
   :description "Middleware to convert the request body to kebab and responses back to snake"
   :wrap (fn [handler]
           (fn [req]
             (let [res (handler (assoc req :parameters
                                   (u/kebab-keys (:parameters req))))]
               (assoc res :body (u/snake_keys (:body res))))))})