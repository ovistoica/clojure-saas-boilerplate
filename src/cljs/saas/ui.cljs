(ns saas.ui
  "This namespace hosts the design system components. All components are based on tailwind"
  (:require [clojure.string :as string]
            [reagent.core :as r]
            [saas.events :as events]
            [saas.subs :as subs]
            [saas.ui.forms :refer [label input checkbox]]
            [saas.ui.buttons :refer [button]]
            [re-frame.core :as rf]
            ["@headlessui/react" :refer [Transition Dialog]]
            ["react" :refer [Fragment]]
            [saas.ui.util :as u]))


;;;;;;;;;;;;;;;;;;;;;;;;;; Form Components ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


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
       "Sign in"]]

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
            (u/clsx
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
      {:class (u/clsx "flex flex-col flex-grow border-r border-gray-200"
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


