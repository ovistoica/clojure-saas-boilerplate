(ns saas.schema
  (:require [malli.core :as m]))

(def email [:re #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"])

(def password string?)

(def account-response
  "The response schema for an account."
  [:map
   [:email email]
   [:password password]
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
