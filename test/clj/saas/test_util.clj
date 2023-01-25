(ns saas.test-util
  (:require [clojure.test :refer :all]
            [integrant.repl.state :as state]
            [muuntaja.core :as m]
            [next.jdbc :as jdbc]
            [saas.util :as util]
            [ring.mock.request :as mock]))

(def token (atom nil))

(def db (-> state/system :saas/db))

(defn test-endpoint
  ([method path]
   (test-endpoint method path {}))
  ([method path {:keys [auth body]}]
   (let [app (-> state/system :saas/handler)
         req (-> (mock/request method path)
                 (mock/header :content-type "application/json")
                 (cond->
                   auth (mock/header :authorization (str "Bearer " @token))
                   body (mock/json-body body)))]
     (-> (app req)
         (update :body (partial m/decode "application/json"))))))

(comment

  (test-endpoint :post "/v1/account/sign-up" {:body {:email "ovidiu.stoica1094+123426782@gmail.com"
                                                     :password "Pa$$w0rd123"
                                                     :first_name "hello"
                                                     :last_name "world"}})


  (util/execute! db {:select [:*]
                     :from [:account]})

  (util/execute! db {:insert-into :account
                     :values {:email "test" :password "hello" :first_name "world" :last_name "world"}})
  )