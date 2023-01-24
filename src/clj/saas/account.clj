(ns saas.account
  (:require [saas.auth :as auth]))

(defn sign-up!
  [auth]
  (fn [req]
    (let [params (-> req :parameters :body)
          result (auth/create-cognito-account auth params)])))
