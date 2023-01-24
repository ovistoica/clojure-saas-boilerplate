(ns saas.auth
  (:require
    [clojure.data.json :as json]
    [cognitect.aws.client.api :as aws]
    [integrant.core :as ig])
  (:import (com.auth0.jwk GuavaCachedJwkProvider UrlJwkProvider)
           (com.auth0.jwt JWT)
           (com.auth0.jwt.algorithms Algorithm)
           (com.auth0.jwt.interfaces RSAKeyProvider)
           (java.nio.charset StandardCharsets)
           (java.util Base64)
           (javax.crypto Mac)
           (javax.crypto.spec SecretKeySpec)))

(defn validate-signature
  [{:keys [^RSAKeyProvider key-provider]} ^String token]
  (let [algorithm (Algorithm/RSA256 key-provider)
        verifier (.build (JWT/require algorithm))
        verified-token (.verify verifier token)]
    (.getPayload verified-token)))

(defn decode-to-str
  [^String s]
  (String. (.decode (Base64/getUrlDecoder) s)))

(defn decode-token
  [token]
  (-> token
      (decode-to-str)
      (json/read-str)))

(defn verify-payload
  [{:keys [config]} {:strs [client_id iss token_use] :as payload}]
  (when-not
    (and
      (= (:client-id config) client_id)
      (= (:jwks config) iss)
      (contains? #{"access" "id"} token_use))
    (throw (ex-info "Token verification failed" {})))
  payload)

(defn verify-and-get-payload
  [auth token]
  (->> token
       (decode-to-str)
       (validate-signature auth)
       (decode-token)
       (verify-payload auth)))

(defn calculate-secret-hash
  [{:keys [client-id client-secret username]}]
  (try
    (let [hmac-sha256-algorithm "HmacSHA256"
          signing-key (SecretKeySpec. (.getBytes client-secret StandardCharsets/UTF_8) hmac-sha256-algorithm)
          mac (doto (Mac/getInstance hmac-sha256-algorithm)
                (.init signing-key)
                (.update (.getBytes username)))
          raw-mac (.doFinal mac (.getBytes client-id StandardCharsets/UTF_8))]
      (.encodeToString (Base64/getEncoder) raw-mac))
    (catch Exception e
      (throw (ex-info "Error while calculating secret hash"
                      {:message (ex-message e)}
                      e)))))

(defn when-anomaly-throw
  [result]
  (when (contains? result :cognitect.anomalies/category)
    (throw (ex-info (:__type result) result))))

(defn create-cognito-account [{:keys [config cognito-idp]} {:keys [email password]}]
  (let [client-id (-> config :client-id)
        client-secret (-> config :client-secret)
        result (aws/invoke cognito-idp
                           {:op :SignUp
                            :request {:ClientId client-id
                                      :Username email
                                      :Password password
                                      :SecretHash (calculate-secret-hash
                                                    {:client-id client-id
                                                     :client-secret client-secret
                                                     :username email})}})]
    (when-anomaly-throw result)
    {:uid (:UserSub result)
     :email email}))

(defn confirm-cognito-account
  [{:keys [config cognito-idp]} {:keys [confirmation-code email]}]
  (let [client-id (-> config :client-id)
        client-secret (-> config :client-secret)
        result (aws/invoke cognito-idp
                           {:op :ConfirmSignUp
                            :request {:ClientId client-id
                                      :Username email
                                      :ConfirmationCode confirmation-code
                                      :SecretHash (calculate-secret-hash
                                                    {:client-id client-id
                                                     :client-secret client-secret
                                                     :username email})}})]
    (when-anomaly-throw result)))

(defn cognito-log-in
  [{:keys [config cognito-idp]} {:keys [email password]}]
  (let [client-id (-> config :client-id)
        client-secret (-> config :client-secret)
        user-pool-id (-> config :user-pool-id)
        result (aws/invoke cognito-idp
                           {:op :AdminInitiateAuth
                            :request {:ClientId client-id
                                      :UserPoolId user-pool-id
                                      :AuthFlow "ADMIN_USER_PASSWORD_AUTH"
                                      :AuthParameters {"USERNAME" email
                                                       "PASSWORD" password
                                                       "SECRET_HASH" (calculate-secret-hash
                                                                       {:client-id client-id
                                                                        :client-secret client-secret
                                                                        :username email})}}})]
    (when-anomaly-throw result)
    (:AuthenticationResult result)))


(defn cognito-refresh-token
  [{:keys [config cognito-idp]} {:keys [refresh-token sub]}]
  (let [client-id (-> config :client-id)
        client-secret (-> config :client-secret)
        user-pool-id (-> config :user-pool-id)
        result (aws/invoke cognito-idp
                           {:op :AdminInitiateAuth
                            :request {:ClientId client-id
                                      :UserPoolId user-pool-id
                                      :AuthFlow "REFRESH_TOKEN_AUTH"
                                      :AuthParameters {"REFRESH_TOKEN" refresh-token
                                                       "SECRET_HASH" (calculate-secret-hash
                                                                       {:client-id client-id
                                                                        :client-secret client-secret
                                                                        :username sub})}}})]
    (when-anomaly-throw result)
    (:AuthenticationResult result)))

(defn cognito-update-role
  [{:keys [config cognito-idp]} claims]
  (let [client-id (-> config :client-id)
        client-secret (-> config :client-secret)
        user-pool-id (-> config :user-pool-id)
        {:strs [sub]} claims
        result (aws/invoke cognito-idp
                           {:op :AdminAddUserToGroup
                            :request {:ClientId client-id
                                      :UserPoolId user-pool-id
                                      :Username sub
                                      :GroupName "cheffs"
                                      :SecretHash (calculate-secret-hash
                                                    {:client-id client-id
                                                     :client-secret client-secret
                                                     :username sub})}})]
    (when-anomaly-throw result)
    result))

(defn cognito-delete-user
  [{:keys [cognito-idp config]} claims]
  (let [user-pool-id (:user-pool-id config)
        {:strs [sub]} claims
        result (aws/invoke cognito-idp
                           {:op :AdminDeleteUser
                            :request
                            {:UserPoolId user-pool-id
                             :Username sub}})]
    (when-anomaly-throw result)))

(defmethod ig/init-key :auth/cognito
  [_ {:keys [jwks] :as config}]
  (let [key-provider (-> ^String jwks
                         (UrlJwkProvider.)
                         (GuavaCachedJwkProvider.))]
    {:cognito-idp (aws/client {:api :cognito-idp})
     :key-provider (reify RSAKeyProvider
                     (getPublicKeyById [_ kid]
                       (.getPublicKey (.get key-provider kid)))

                     (getPrivateKey [_]
                       nil)

                     (getPrivateKeyId [_]
                       nil))
     :config config}))
