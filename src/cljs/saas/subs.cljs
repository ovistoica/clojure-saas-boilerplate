(ns saas.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  ::name
  (fn [db]
    (:name db)))


(rf/reg-sub
  ::dark-mode?
  (fn [db]
    (:dark-mode? db)))



(rf/reg-sub
  :app/show-mobile-sidebar?
  (fn [db _]
    (get-in db [:app :show-mobile-sidebar?])))
