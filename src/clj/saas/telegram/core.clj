(ns saas.telegram.core
  (:require [martian.core :as martian]
            [martian.hato :as mh]))

(def m (mh/bootstrap-openapi "Telegram Bot API-OpenApi3Json.json"))

(martian/explore m :send-message-post)


(martian/request-for m :send-message-post {:chat-id 123 :text "Hello World"})