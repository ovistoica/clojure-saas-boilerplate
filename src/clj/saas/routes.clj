(ns saas.routes
  (:require [saas.account :as account]))

(defn routes
  [system]
  (let [auth (:auth/cognito system)]
    ["/account" {:swagger {:tags ["account"]}}
     [""
      {:put {}
       :delete {}}]
     ["/confirm" {:post {}}]
     ["/log-in" {:post {}}]
     ["/refresh" {:post {}}]
     ["/sign-up" {:post {:summary "Create an account using cognito"
                         :handler (account/sign-up! auth)
                         :responses {201 {:body {:account-id string?}}}
                         :parameters {:body {:email string?
                                             :password string?}}}}]]))