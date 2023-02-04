(ns saas.ui
  "This namespace hosts the design system components. All components are based on tailwind"
  (:require [clojure.string :as string]
            [reagent.core :as r]))

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

(comment
  (clsx "hello" "world" "world hello" " yagg ")
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

(def button-variants
  {:base-style "inline-flex items-center rounded  shadow-sm"
   :type {:primary "bg-brand-600 text-white hover:bg-brand-700 border border-transparent"
          :secondary "bg-white hover:bg-slate-100 hover:text-brand-700 text-slate-900 border border-gray-200 dark:text-slate-200 dark:bg-slate-800  dark:hover:bg-slate-700 dark:hover:text-white dark:border-slate-700"}
   :focus {:true "focus:outline-none focus:ring-none"
           :false ""}
   :disabled? {:true "opacity-50 cursor-not-allowed"
               :false ""}
   :size {:xs "px-2.5 py-1.5 text-xs"
          :sm "px-3 py-2 text-sm"
          :md "px-4 py-2 text-sm"
          :lg "px-4 py-2 text-base"
          :xl "px-6 py-3 text-base"}
   :full-width {:true "w-full"}
   :default-variants {:type :primary
                      :size :md
                      :focus :true}})


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

(def loading-variants
  {:base-style "w-4 h-4 animate-spin inline mr-3"
   :type {:primary "text-slate-200 fill-white dark:text-slate-600"
          :secondary "text-slate-200 fill-brand-700 dark:text-slate-600"}
   :default-variants {:type :primary}})

(defn loading-icon
  [props]
  (let [classes (get-classes loading-variants props)]
    [:svg {:class-name classes :aria-hidden "true" :viewBox "0 0 100 101" :fill "none" :xmlns "http://www.w3.org/2000/svg"}
     [:path {:d "M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z" :fill "currentColor"}]
     [:path {:d "M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z" :fill "currentFill"}]]))

(defn button
  [& _]
  (let [this (r/current-component)
        props (r/props this)
        children (r/children this)
        {:keys [label on-click loading? disabled?]} props
        classes (get-classes button-variants
                             (dissoc props :label :on-click))]
    [:button {:disabled (or loading? disabled?)
              :class-name classes
              :on-click on-click}
     (when loading?
       [loading-icon props])
     (if loading?
       "Loading..."
       (or label (render-children children)))]))

(defn container
  "Full width on mobile, constrained with padded content above"
  [& _]
  (let [this (r/current-component)
        {:keys [class class-name]} (r/props this)
        children (r/children this)]
    [:div {:class-name (clsx "mx-auto w-full h-screen p-4"
                             (or class-name class))}
     (render-children children)]))