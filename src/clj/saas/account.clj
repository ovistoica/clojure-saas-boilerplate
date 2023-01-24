(ns saas.account
  (:require
    [ring.util.response :as rr]
    [saas.auth :as auth]
    [saas.account.db :as db]))

(defn sign-up!
  [{:keys [auth db]}]
  (fn [req]
    (let [params (-> req :parameters :body)
          {:keys [uid] :as result} (auth/create-cognito-account auth params)
          account (merge params result)
          _ (db/insert-account! db account)]
      (rr/created (str "/account/" uid) (dissoc account :password)))))
