(ns saas.account.handlers
  (:require
   [ring.util.response :as rr]
   [saas.account.db :as db]
   [saas.auth :as auth]))

(defn sign-up!
  [{:keys [auth db]}]
  (fn [req]
    (let [params (-> req :parameters :body)
          {:keys [uid] :as result} (auth/create-cognito-account auth params)
          account (merge params result)
          _ (db/insert-account! db account)]
      (rr/created (str "/account/" uid) (dissoc account :password)))))

(defn log-in
  [auth]
  (fn [req]
    (let [params (-> req :parameters :body)
          result (auth/cognito-log-in auth params)]
      (rr/response {:token (:AccessToken result)
                    :refresh-token (:RefreshToken result)}))))

(defn confirm-account
  [auth]
  (fn [req]
    (let [params (-> req :parameters :body)
          _ (auth/confirm-cognito-account auth params)]
      (rr/status 204))))

(defn refresh-token
  [auth]
  (fn [req]
    (let [refresh-token (-> req :parameters :body :refresh-token)
          sub (get-in req [:claims "sub"])
          cognito-refresh-token
          (auth/cognito-refresh-token auth {:refresh-token refresh-token
                                            :sub sub})]
      (rr/response cognito-refresh-token))))