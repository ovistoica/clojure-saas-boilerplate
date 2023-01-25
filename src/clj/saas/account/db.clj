(ns saas.account.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn insert-account!
  "Insert an account into the database."
  [db {:keys [email first-name last-name uid] :as _account}]
  (sql/insert! db :account {:uid uid
                             :email email
                             :first_name first-name
                             :last_name last-name}))
