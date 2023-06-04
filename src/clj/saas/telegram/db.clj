(ns saas.telegram.db
  (:require [integrant.repl.state :as state]
            [saas.db.utils :refer [execute-one! execute!]]
            [honey.sql :as hsql]
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

(defn get-telegram-user
  [db user-id]
  (execute-one! db {:select [:*]
                    :from :telegram_user
                    :where [:= :id user-id]} {:debug true}))

(defn insert-telegram-user!
  "Inserts a new telegram user into the database. See schema/telegram-user for details
   user: {:id 123 :first-name \"John\" :last-name \"Doe\" :username \"johndoe\" :is-bot false}"
  [db user]
  (execute-one! db {:insert-into :telegram_user
                    :values [user]} {:debug true}))

(defn insert-calorie-log!
  [db calorie-log]
  (execute-one! db {:insert-into :calories
                    :values [calorie-log]} {:debug true}))

(defn select-today-calorie-entries
  [db user-id]
  (execute!
   db
   {:select [:*]
    :from :calories
    :where [:and [:= :user_id user-id]
            [:>= :%date.log_time
             :CURRENT_DATE]]}
   {:debug true}))

(comment

 (->> [{:id 1,
        :user-id 1641544258M,
        :food-input "300g of carbonara pasta",
        :log-time #inst"2023-06-03T08:52:29.000000000-00:00",
        :calories 1110M,
        :proteins 36M,
        :carbs 145M,
        :fat 44M}
       {:id 2,
        :user-id 1641544258M,
        :food-input "100g of chicken breast",
        :log-time #inst"2023-06-03T08:59:51.000000000-00:00",
        :calories 165M,
        :proteins 31M,
        :carbs 0M,
        :fat 3.6M}]
      )

 (hsql/format {:select [:*]
               :from :calories
               :where [:and [:= :user_id 123]
                       [:>= :%date.log_time :CURRENT_DATE]]})

 (insert-telegram-user! (-> state/system :saas/db) {:first_name "Ovidiu",
                                                    :id 1641544258,
                                                    :is_bot false,
                                                    :language_code "en",
                                                    :last_name "Stoica",
                                                    :username "ovistoica"})

 (get-telegram-user (-> state/system :saas/db) 1641544258)
 (get-telegram-user (-> state/system :saas/db) 164154428)


 (def user-id 1641544258)
 (select-today-calorie-entries (-> state/system :saas/db) user-id)

 (upsert-chat-last-update-id! (-> state/system :saas/db) {:update-id 3 :chat-id 2})

 (get-latest-update-id (-> state/system :saas/db) 2)
 )
