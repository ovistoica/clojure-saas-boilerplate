(ns saas.schema
  (:require [malli.core :as m]))

(def email [:re #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"])

(def password string?)


(def confirmation-code
  "6 digit string => 123456"
  [:re #"^[0-9]{6,6}$"])

(def account-response
  "The response schema for an account."
  [:map
   [:uid string?]
   [:email email]
   [:first_name [:maybe string?]]
   [:last_name [:maybe string?]]])

(def account-list-response
  "The response schema for a list of accounts."
  [:sequential account-response])

(def create-account-request
  "The request schema for creating an account."
  [:map
   [:email email]
   [:password password]
   [:first_name {:optional true} [:maybe string?]]
   [:last_name {:optional true} [:maybe string?]]])

(def log-in-request-body
  [:map
   [:email email]
   [:password password]])

(def log-in-response
  [:map
   [:token string?]
   [:refresh-token string?]])

(def confirm-account-request-body
  [:map
   [:email email]
   [:confirmation_code confirmation-code]])