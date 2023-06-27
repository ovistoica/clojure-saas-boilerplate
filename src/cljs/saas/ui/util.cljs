(ns saas.ui.util
  (:require [clojure.string :as string]
            [reagent.core :as r]
            ["tailwind-merge" :refer [twMerge]]))

(defn props? [x]
  (map? x))

(defn clsx
  "Takes an array of tw classes and returns the joined classes with no duplicates"
  [& classes]
  (->> classes
       (flatten)
       (map #(string/split % #" "))
       (flatten)
       (remove #(or (nil? %) (empty? %)))
       (distinct)
       (string/join " ")))

(defn tw
  [& input]
  (-> input
      (clsx)
      (twMerge)))

(comment
 (clsx "hello" "world" "world hello" " yagg ")

 (tw "px-2 py-1 bg-red hover:bg-dark-red", "p-3 bg-[#B91C1C]")
 )

(defn render-children
  [children]
  (for [child children]
    child))

(defn strip-unused-props
  [class-variants props]
  (->> (dissoc class-variants :default-variants :base-style)
       (keys)
       (select-keys props)))

(def get-classes
  "Utility function helping to turn component props into tailwind classes"
  (memoize
   (fn [{:keys [base-style default-variants] :as variant-config} props]
     (let [p (merge default-variants
                    (strip-unused-props variant-config props))
           p-keys (keys p)]
       (->> (for [pk p-keys
                  :let [val (cond->> (get p pk))
                        classes (get-in variant-config [pk val])]]
              classes)
            (cons base-style)
            (clsx))))))

(defn variants->style-props
  "Transform component variants map declaration into
  a map showing possible props"
  [cv]
  (let [prop-keys (keys (dissoc cv :default-variants :base-style))]
    (-> (reduce (fn [acc pk]
                  (let [variants (-> (get cv pk)
                                     (keys))]
                    (assoc acc pk (vec variants))))
                {}
                prop-keys)
        (assoc :_defaults (:default-variants cv)))))

(defn props-children
  []
  (let [this (r/current-component)
        props (r/props this)
        children (r/children this)]
    {:props props
     :children children}))
