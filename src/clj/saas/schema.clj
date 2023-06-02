(ns saas.schema
  (:require [malli.swagger :as swagger]))

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

(def telegram-webhook-response
  [:enum "ok"])

(def telegram-user
  [:map
   [:id number?]
   [:is_bot boolean?]
   [:first_name string?]
   [:last_name {:optional true} string?]
   [:username {:optional true} string?]
   [:language_code {:optional true} string?]
   [:can_join_groups {:optional true} boolean?]
   [:can_read_all_group_messages {:optional true} boolean?]
   [:supports_inline_queries {:optional true} boolean?]])

(def telegram-chat-permissions
  [:map
   [:can_send_messages {:optional true} boolean?]
   [:can_send_media_messages {:optional true} boolean?]
   [:can_send_polls {:optional true} boolean?]
   [:can_send_other_messages {:optional true} boolean?]
   [:can_add_web_page_previews {:optional true} boolean?]
   [:can_change_info {:optional true} boolean?]
   [:can_invite_users {:optional true} boolean?]
   [:can_pin_messages {:optional true} boolean?]])

(def telegram-chat-type [:enum "private" "group" "supergroup" "channel"])

(def telegram-location
  [:map
   [:longitude {:description "Longitude as defined by sender"} :double]
   [:latitude {:description "Latitude as defined by sender"} :double]
   [:horizontal_accuracy
    {:description "*Optional*. The radius of uncertainty for the location, measured in meters; 0-1500",
     :optional true} :double]
   [:live_period {:description "*Optional*. Time relative to the message sending date, during which the location can be updated, in seconds. For active live locations only.",
                  :format "int32",
                  :optional true} :int]
   [:heading
    {:description "*Optional*. The direction in which user is moving, in degrees; 1-360. For active live locations only.", :format "int32", :optional true} :int]
   [:proximity_alert_radius {:description "*Optional*. Maximum distance for proximity alerts about approaching another chat member, in meters. For sent live locations only.", :format "int32", :optional true} :int]])

(def telegram-chat-location
  [:map
   [:location
    {:description "Represents a location to which a chat is connected."}
    telegram-location]
   [:address
    {:description "Location address; 1-64 characters, as defined by the chat owner"}
    :string]])

(def telegram-type
  [:enum
   "mention"
   "hashtag"
   "cashtag"
   "bot_command"
   "url"
   "email"
   "phone_number"
   "bold"
   "italic"
   "underline"
   "strikethrough"
   "code"
   "pre"
   "text_link"
   "text_mention"])

(def telegram-message-entity
  [:map
   [:type {:description "Type of the entity"} telegram-type]
   [:offset
    {:description "Offset in UTF-16 code units to the start of the entity",
     :format "int32"}
    :int]
   [:length
    {:description "Length of the entity in UTF-16 code units",
     :format "int32"}
    :int]
   [:url
    {:description "*Optional*. For “text\\_link” only, url that will be opened after user taps on the text",
     :optional true}
    :string]
   [:user
    {:description "*Optional*. User that will be linked",
     :optional true}
    telegram-user]
   [:language
    {:description "*Optional*. For “pre” only, the programming language of the entity text",
     :optional true}
    :string]])



(def telegram-chat
  [:map
   [:id integer?]
   [:type telegram-chat-type]
   [:title {:optional true} string?]
   [:username {:optional true} string?]
   [:first_name {:optional true} string?]
   [:last_name {:optional true} string?]
   [:photo {:optional true} string?]
   [:description {:optional true} string?]
   [:invite_link {:optional true} string?]
   [:pinned_message {:optional true} :any]
   [:permissions {:optional true} telegram-chat-permissions]
   [:slow_mode_delay {:optional true} integer?]
   [:sticker_set_name {:optional true} string?]
   [:can_set_sticker_set {:optional true} boolean?]
   [:linked_chat_id {:optional true} integer?]
   [:location {:optional true} telegram-chat-location]])

(def telegram-message
  [:map
   [:message_id :int]
   [:from {:optional true} telegram-user]
   [:sender_chat {:optional true} telegram-chat]
   [:date :int]
   [:chat telegram-chat]
   [:forward_from {:optional true} telegram-user]
   [:forward_from_chat {:optional true} telegram-chat]
   [:forward_from_message_id {:optional true} :int]
   [:forward_signature {:optional true} :string]
   [:forward_sender_name {:optional true} :string]
   [:forward_date {:optional true} :int]
   [:via_bot {:optional true} telegram-user]
   [:reply_to_message {:optional true} :any]
   [:media_group_id {:optional true} :string]
   [:author_signature {:optional true} :string]
   [:text {:optional true} :string]
   [:entities {:optional true} [:vector telegram-message-entity]]])


(def telegram-webhook-request
  [:map
   [:update_id number?]
   [:message telegram-message]])

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

(comment
 (swagger/transform (slurp "resources/Telegram Bot API-OpenApi3Json.json"))

 )