(ns saas.telegram.db
  (:require [integrant.repl.state :as state]
            [saas.db.utils :refer [execute-one!]]
            [honey.sql.helpers :refer [select from where]]))

(defn get-latest-update-id
  [db chat-id]
  (-> (execute-one!
       db
       (-> (select :last-update-id)
           (from :telegram_updates)
           (where [:= :chat_id chat-id])) {:debug true})
      :last_update_id))

(defn upsert-chat-last-update-id!
  [db {:keys [update-id chat-id]}]
  (execute-one! db
                {:insert-into :telegram_updates
                 :values [{:chat_id chat-id :last_update_id update-id}]
                 :on-conflict :chat_id
                 :do-update-set :last_update_id} {:debug true}))

(comment
 (upsert-chat-last-update-id! (-> state/system :saas/db) {:update-id 3 :chat-id 2})

 (get-latest-update-id (-> state/system :saas/db) 2)
 )
