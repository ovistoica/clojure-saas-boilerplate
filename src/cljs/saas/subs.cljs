(ns saas.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::name
  (fn [db]
    (:name db)))


(re-frame/reg-sub
  ::dark-mode?
  (fn [db]
    (:dark-mode? db)))