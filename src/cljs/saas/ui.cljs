(ns saas.ui
  "This namespace hosts the design system components. All components are based on tailwind"
  (:require [clojure.string :as string]))

(defn get-classes
  "Utility function helping to turn component props into tailwind classes"
  [{:keys [base-style] :as variant-config} props]
  (let [defaults (:default-variants variant-config)
        p (merge defaults props)
        p-keys (keys p)]
    (->> (for [pk p-keys
               :let [val (cond->> (get p pk))
                     classes (get-in variant-config [pk val])]]
           classes)
         (cons base-style)
         (map #(string/split % #" "))
         (flatten)
         (vec)
         (string/join " "))))

(def button-variants
  {:base-style (str "inline-flex items-center rounded border border-transparent shadow-sm")
   :type {:primary "bg-brand-600 text-white hover:bg-brand-700"
          :secondary "text-brand-700 border border-transparent bg-brand-100 hover:bg-brand-200"}
   :focus {:true "focus:outline-none focus:ring-2 focus:ring-brand-500 focus:ring-offset-2"
           :false ""}
   :loading {}
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

(defn button
  [{:keys [label on-click] :as props}]
  (let [classes (get-classes button-variants (dissoc props :label :on-click))]
    [:button {:class-name classes
              :on-click on-click}
     label]))