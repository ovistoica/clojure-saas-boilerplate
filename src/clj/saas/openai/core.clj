(ns saas.openai.core
  (:require
   [martian.core :as martian]))

(defn response-for
  [m operation params {:or {}
                       :as options}]
  (-> (martian/response-for m operation (assoc params ::options options))
      :body))

