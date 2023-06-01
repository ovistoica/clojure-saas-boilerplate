(ns saas.routes
  (:require [saas.account.handlers :as account]
            [saas.telegram.handlers :as telegram]
            [saas.middleware :as mw]
            [saas.schema :as s]))


(defn saas-routes
  [{:keys [auth] :as system}]
  [["/telegram-webhook" {:swagger {:tags ["telegram"]}}
    ["" {:post {:summary "Telegram webhook"
                 :description "Telegram webhook"
                 :parameters {:body s/telegram-webhook-request}
                 :handler (telegram/telegram-webhook-handler system)}}]]
   ["/account" {:swagger {:tags ["account"]}
                :middleware [[mw/wrap-snake->kebab->snake]]}
    ["/sign-up" {:post {:summary "Create an account using cognito"
                        :description "Create an account using cognito"
                        :handler (account/sign-up! system)
                        :responses {201 {:body s/account-response}}
                        :parameters {:body s/create-account-request}}}]
    ["/confirm" {:post {:summary "Confirm your account"
                        :description "Confirm an account by using the access code
                       that was provided through email"
                        :handler (account/confirm-account auth)
                        :responses {204 {:body nil}}
                        :parameters {:body s/confirm-account-request-body}}}]
    ["/log-in" {:post {:summary "Log in to your account"
                       :description "Log in using your email and password"
                       :handler (account/log-in auth)
                       :responses {200 {:body s/log-in-response}}
                       :parameters {:body s/log-in-request-body}}}]
    ["/refresh" {:post {:summary "Refresh token"
                        :description "Refresh your token using the `refresh_token`
                       provided at log-in"
                        :handler (account/refresh-token auth)}}]]])