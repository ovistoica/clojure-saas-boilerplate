(ns saas.ui.button-shad
  (:require
   ["react" :as react]
   ["@radix-ui/react-slot" :refer [Slot]]
   [reagent.core :as r]
   [saas.ui.util :as u]))

(def button-variants
  {:base-style "inline-flex items-center justify-center rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50"
   :variant {:default "bg-primary text-primary-foreground hover:bg-primary/90"
             :destructive "bg-destructive text-destructive-foreground hover:bg-destructive/80"
             :outline "border border-input bg-background hover:bg-accent hover:text-accent-foreground",
             :secondary "bg-secondary text-secondary-foreground hover:bg-secondary/80",
             :ghost "hover:bg-accent hover:text-accent-foreground"
             :link "text-primary underline-offset-4 hover:underline"}
   :size {:default "h-10 px-4 py-2",
          :sm "h-9 rounded-md px-3",
          :lg "h-11 rounded-md px-8",
          :icon "h-10 w-10",}
   :default-variants {:variant :default
                      :size :default}})

(comment
 (u/get-classes button-variants))

(def slot (r/adapt-react-class Slot))

(defn button
  [& _]
  (let [{:keys [props children]} (u/props-children)
        {:keys [as-child class]} props
        comp (if as-child slot :button)
        rest (dissoc props :class :variant :size :as-child)
        classes (u/get-classes
                 button-variants
                 (select-keys props [:variant :size]))
        component-props (merge {:class (u/tw class classes)} rest)
        _ (println children)]
    [comp component-props
     (u/render-children children)]))