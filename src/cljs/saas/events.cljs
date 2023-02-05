(ns saas.events
  (:require [re-frame.core :as rf]
            [goog.dom.classlist :as gc]
            [saas.db :as db]))

(rf/reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))

(rf/reg-event-db
  ::toggle-mobile-sidebar
  (fn [db _]
    (let [new-state (not (get-in db [:app :show-mobile-sidebar?]))]
      (assoc-in db [:app :show-mobile-sidebar?] new-state))))

(rf/reg-event-fx
  ::toggle-dark-mode
  (fn [{:keys [db]} _]
    (let [val (:dark-mode? db)]
      {:db (assoc db :dark-mode? (not val))
       ::toggle-dark-mode-dom (not val)})))

(rf/reg-fx
  ::toggle-dark-mode-dom
  (fn [val]
    (prn "this got called")
    (if val
      (gc/add js/document.body "dark")
      (gc/remove js/document.body "dark"))))
