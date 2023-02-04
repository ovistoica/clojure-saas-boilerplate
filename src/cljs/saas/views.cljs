(ns saas.views
  (:require
    [re-frame.core :as rf]
    [saas.subs]
    [saas.events :as events]
    [saas.ui :as ui]))

(defn main-panel []
  [ui/container {:class "bg-red-100"}
   [ui/button {:label "Click me!"
               :type :secondary
               :on-click #(rf/dispatch [::events/toggle-dark-mode])
               :full-width true
               :size :xl}]])