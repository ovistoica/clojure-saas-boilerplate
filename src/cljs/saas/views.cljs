(ns saas.views
  (:require
    [re-frame.core :as rf]
    [saas.subs]
    [saas.events :as events]
    [saas.ui :as ui]))

(defn main-panel []
  [ui/container
   [ui/button {:label "Click me!"
               :type :secondary
               :on-click #(rf/dispatch [::events/toggle-dark-mode])
               :full-width true
               :size :xl}]

   [:div.mt-4
    [ui/label {:for "email"} "Email"]
    [ui/input {:type "email"
               :name "email"
               :id "email"
               :placeholder ""}]]])
