(ns saas.views
  (:require
    [re-frame.core :as re-frame]
    [saas.subs :as subs]
    [saas.ui :as ui]))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div.p-4
     [:h1 "Hello from " @name]
     [ui/button {:label "Click me!" :type :secondary}]]))

