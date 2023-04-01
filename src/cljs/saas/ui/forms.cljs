(ns saas.ui.forms
  (:require [reagent.core :as r]
            [reagent.impl.component :as component]
            [saas.ui.util :as u]))

(def label-variants
  {:base-style "block text-sm font-medium text-slate-700 dark:text-gray-200"
   :error? {:true "text-red-500 font-normal"}})

(defn label
  "Form label"
  [& _]
  (let [this (r/current-component)
        props (r/props this)
        children (r/children this)
        {:keys [for]} props
        classes (u/get-classes label-variants props)]
    [:label {:for for :class-name classes}
     (u/render-children children)]))

(def input-variants
  {:base-style "block w-full rounded-md border-slate-300 shadow-sm focus:border-brand-500 focus:ring-brand-500 sm:text-sm"
   :error? {:true "border-red-500"}
   :full-width? {:true "w-full"}})

(defn- input-props
  [props]
  (select-keys [:type :name :id :input-class :placeholder :error? :full-width?] props))

(defn input
  [{:keys [type name id class-name class placeholder] :as props}]
  (let [classes (u/get-classes input-variants props)]
    [:div.mt-1
     [:input {:type type
              :name name
              :id id
              :class-name (u/clsx classes
                                  class-name
                                  class)
              :placeholder placeholder}]]))

(defn- label?
  [child]
  (= label (first child)))

(defn- input?
  [child]
  (= input (first child)))

(defmulti add-props (fn [child _] child))
(defmethod add-props :label
  [[child child-props & rest] {:keys [id required?]}]
  (let [lp (if (map? child-props) child-props {})]
    [child (merge lp {:for id :required? required?})
     (spread rest)])
  )

(defn form-control
  [& _]
  (let [{:keys [props children]} (u/props-children)
        _ (prn children)
        f-child (first (first children))
        _ (prn (= f-child label))
        {:keys [id error? error-label required? name]} props
        ip (input-props props)]
    [:div
     (u/render-children children)]))

(defn checkbox
  [{:keys [id name class label]}]
  [:div.flex.items-center
   [:input {:name name
            :id id
            :type "checkbox"
            :class (u/clsx "h-4 w-4 rounded border-slate-300"
                           "text-brand-600 focus:ring-brand-500"
                           class)}]
   (when label
     [:label {:html-for id
              :class "ml-2 block text-sm text-slate-900"} label])])
