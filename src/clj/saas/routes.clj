(ns saas.routes
  (:require [saas.account :as account]
            [saas.schema :as s]))

(defn saas-routes
  [system]
  ["/account" {:swagger {:tags ["account"]}}
   ["/sign-up" {:post {:summary "Create an account using cognito"
                       :description "Create an account using cognito"
                       :handler (account/sign-up! system)
                       :responses {201 {:body s/account-response}}
                       :parameters {:body s/create-account-request}}}]])