(ns saas.account.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn insert-account!
  "Insert an account into the database."
  [db {:keys [email password first-name last-name]}]
  (sql/insert! db :accounts {:email email
                             :password password
                             :first_name first-name
                             :last_name last-name}))
