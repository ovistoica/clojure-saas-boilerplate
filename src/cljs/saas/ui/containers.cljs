(ns saas.ui.containers
  (:require [reagent.core :as r]
            [saas.ui.util :as u]))

(defn container
  "Full width on mobile, constrained with padded content above"
  [& _]
  (let [this (r/current-component)
        {:keys [class class-name]} (r/props this)
        children (r/children this)]
    [:div {:class (u/clsx "mx-auto w-full h-screen p-4"
                               (or class-name class))}
     (u/render-children children)]))