(ns saas.ui
  "This namespace hosts the design system components. All components are based on tailwind"
  (:require [clojure.string :as string]
            [reagent.core :as r]
            [saas.events :as events]
            [saas.subs :as subs]
            [re-frame.core :as rf]
            ["@headlessui/react" :refer [Transition Dialog]]
            ["react" :refer [Fragment]]))


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
   :intent {:primary "bg-brand-600 text-white hover:bg-brand-700 border border-transparent"
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
   :default-variants {:intent :primary
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
        {:keys [label on-click loading? disabled? type class]} props
        classes (get-classes button-variants
                             (dissoc props :label :on-click))]
    [:button {:disabled (or loading? disabled?)
              :class-name (clsx classes class)
              :on-click on-click
              :type type}
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

;;;;;;;;;;;;;;;;;;;;;;;;;; Form Components ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def label-variants
  {:base-style "block text-sm font-medium text-slate-700 dark:text-gray-200"})

(defn label
  "Form label"
  [& _]
  (let [this (r/current-component)
        props (r/props this)
        children (r/children this)
        {:keys [for]} props
        classes (get-classes label-variants props)]
    [:label {:for for :class-name classes}
     (render-children children)]))

(def input-variants
  {:base-style "block w-full rounded-md border-slate-300 shadow-sm focus:border-brand-500 focus:ring-brand-500 sm:text-sm"
   :full-width {:true "w-full"}})

(defn input
  [{:keys [type name id class-name placeholder] :as props}]
  (let [classes (get-classes input-variants props)
        ]
    [:div.mt-1
     [:input {:type type
              :name name
              :id id
              :class-name (clsx classes
                                class-name)
              :placeholder placeholder}]]))

(defn checkbox
  [{:keys [id name class label]}]
  [:div.flex.items-center
   [:input {:name name
            :id id
            :type "checkbox"
            :class (clsx "h-4 w-4 rounded border-slate-300 text-brand-600 focus:ring-brand-500"
                         class)}]
   (when label
     [:label {:html-for id :class "ml-2 block text-sm text-slate-900"} label])])

(defn authentication
  []
  [:div {:class "flex min-h-full flex-col justify-center py-12 sm:px-6 lg:px-8"}
   [:div {:class "sm:mx-auto sm:w-full sm:max-w-md"}
    [:img {:class "mx-auto h-12 w-auto"
           :src "https://tailwindui.com/img/logos/mark.svg?color=indigo&shade=600"
           :alt "Your Company"}]
    [:h2 {:class "mt-6 text-center text-3xl font-bold tracking-tight text-gray-900"} "Sign in to your account"]
    [:p {:class "mt-2 text-center text-sm text-gray-600"} "Or "
     [:a {:href "#" :className "font-medium text-indigo-600 hover:text-indigo-500"} "start your 14-day free trial"]]]

   [:div {:class "mt-8 sm:mx-auto sm:w-full sm:max-w-md"}
    [:div {:class "bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10"}
     [:form {:action "#" :class "space-y-6" :method "POST"}

      [:div
       [label {:for "email"} "Email"]
       [input {:id "email" :name "email" :type "email" :auto-complete "email" :required true}]]

      [:div
       [label {:for "password"} "Password"]
       [input {:id "password" :name "password" :type "password" :auto-complete "password" :required true}]]

      [:div.flex.items-center.justify-between
       [checkbox {:id "remember-me" :name "remember-me" :label "Remember me"}]

       [:div {:class "text-sm"}
        [:a {:href "#" :class "font-medium text-brand-600 hover:text-brand-500"} "Forgot your password?"]]]

      [button {:full-width :true
               :type :submit
               :class "justify-center"}
       "Sign in"] ]



     ]]

   ])

(def dialog (r/adapt-react-class Dialog))
(def dialog-overlay (r/adapt-react-class (.-Overlay Dialog)))
(def dialog-panel (r/adapt-react-class (.-Panel Dialog)))


(def transition-child
  "Used to coordinate multiple transitions based on the same event.
  Needs to be nested inside a transition-root element."
  (r/adapt-react-class (.-Child Transition)))
(def transition-root
  (r/adapt-react-class (.-Root Transition)))


(def fragment Fragment)

#_(def navigation-elements
    [{:id :home
      :name "Home"
      :icon home-icon
      :dispatch #(rf/dispatch [::events/set-active-page :home])}
     {:id :invoices
      :name "Invoices"
      :icon chart-icon
      :dispatch #(rf/dispatch [::events/set-active-page :invoices])}
     {:id :reports
      :name "Reports"
      :icon chart-icon
      :dispatch #(rf/dispatch [::events/set-active-page :reports])}])

(defn mobile-sidebar
  []
  (let [rf-open? @(rf/subscribe [::subs/show-mobile-sidebar?])
        open? (if (boolean? rf-open?)
                rf-open?
                false)]

    [transition-root {:show open? :as fragment}
     [dialog
      {:as "div"
       :class "fixed inset-0 flex z-40 md:hidden"
       :on-close #(rf/dispatch [::events/show-mobile-sidebar false])}
      [transition-child
       {:as fragment
        :enter "transition-opacity ease-linear duration-300"
        :enterFrom "opacity-0"
        :enterTo "opacity-100"
        :leave "transition-opacity ease-linear duration-300"
        :leaveFrom "opacity-100"
        :leaveTo "opacity-0"}
       [dialog-overlay {:class "fixed inset-0 bg-gray-600 bg-opacity-75"}]]
      [transition-child
       {:as fragment
        :enter "transition ease-in-out duration-300 transform"
        :enterFrom "-translate-x-full"
        :enterTo "translate-x-0"
        :leave "transition ease-in-out duration-300 transform"
        :leaveFrom "translate-x-0"
        :leaveTo "-translate-x-full"}
       [:div
        {:class
         "relative flex-1 flex flex-col max-w-xs w-full pt-5 pb-4 bg-white"}
        [transition-child
         {:as fragment
          :enter "ease-in-out duration-300"
          :enterFrom "opacity-0"
          :enterTo "opacity-100"
          :leave "ease-in-out duration-300"
          :leaveFrom "opacity-100"
          :leaveTo "opacity-0"}
         [:div.absolute.top-0.right-0.-mr-12.pt-2
          [:button
           {:type :button
            :class
            (clsx
              "ml-1 flex items-center justify-center h-10 w-10 rounded-full"
              "focus:outline-none focus:ring-2 focus:ring-inset focus:ring-white")
            :on-click #(rf/dispatch [::events/show-mobile-sidebar false])}
           [:span.sr-only "Close sidebar"]
           [:p {:class "h-6 w-6 text-white" :aria-hidden true} "X"]]]]
        [:div.flex-shrink-0.flex.items-center.px-4
         [:img.h-8.w-auto
          {:src
           "https://designvote-storage.fra1.cdn.digitaloceanspaces.com/logo-transaction.png"
           :alt "Transaction Manager"}]]
        [:div.mt-5.flex-1.h-0.overflow-y-auto
         [:nav.px-2.space-y-1
          #_(for [{:keys [icon current href name]} navigation-elements]
              ^{:key (str "mobile-nav " name)}
              [:a
               {:href href
                :class
                (clsx
                  "group flex items-center px-2 py-2 text-base font-medium rounded-md"
                  (if current
                    "bg-gray-100 text-gray-900"
                    "text-gray-600 hover:bg-gray-50 hover:text-gray-900"))}
               [icon
                {:class (clsx "mr-4 flex-shrink-0 h-6 w-6"
                              (if current
                                "text-gray-500"
                                "text-gray-400 group-hover:text-gray-500"))}]
               name])]]]]
      ; dummy element to force sidebar to shrink to fit close icon
      [:div.flex-shrink-0.w-14 {:aria-hidden true}]]]))



(defn desktop-sidebar
  []
  (let [active-page @(rf/subscribe [:nav/active-page])]
    [:div {:class "hidden md:flex md:w-64 md:flex-col md:fixed md:inset-y-0"}
     [:div
      {:class (clsx "flex flex-col flex-grow border-r border-gray-200"
                    "pt-5 bg-white overflow-y-auto")}
      [:div {:class "flex items-center flex-shrink-0 px-4"}
       [:img
        {:class "h-8 w-auto"
         :src
         "https://designvote-storage.fra1.cdn.digitaloceanspaces.com/logo-transaction.png"
         :alt "Transaction Manager"}]]
      [:div {:class "mt-5 flex-grow flex flex-col"}
       [:nav {:class "flex-1 px-2 pb-4 space-y-1"}
        #_(for [{:keys [icon href name id dispatch]} navigation-elements
                :let [active? (= active-page id)]]
            ^{:key (str "mobile-nav " name)}
            [:a
             {:href href
              :on-click #(dispatch)
              :class
              (clsx
                "group flex items-center px-2 py-2 text-sm font-medium rounded-md"
                (if active?
                  "bg-gray-100 text-gray-900"
                  "text-gray-600 hover:bg-gray-50 hover:text-gray-900"))}
             [icon
              {:class (clsx "mr-3 flex-shrink-0 h-6 w-6"
                            (if active?
                              "text-gray-500"
                              "text-gray-400 group-hover:text-gray-500"))
               :aria-hidden true}] name])]]]]))


#_(defn application-top-bar
    []
    [:div.sticky.top-0.flex-shrink-0.flex.h-16.bg-white.shadow
     [:button
      {:type "button"
       :class (clsx
                "px-4 border-r border-gray-200 text-gray-500 focus:outline-none"
                "focus:ring-2 focus:ring-inset focus:ring-indigo-500 md:hidden")
       :on-click #(rf/dispatch [:app/toggle-mobile-sidebar])}
      [:alt.sr-only "Open sidebar"]
      [menu-icon {:class "h-6 w-6" :aria-hidden true}]]
     [:div.flex-1.px-4.flex.justify-between [:div.flex-1.flex]
      [:div.ml-4.flex.items-center.md:ml-6
       [:button
        {:type :button
         :class
         (clsx
           "bg-white p-1 rounded-full text-gray-400 hover:text-gray-500"
           "focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500")}
        [:span.sr-only "View notifications"]
        [bell-icon {:class "h-6 w-6" :aria-hidden true}]]]]])


