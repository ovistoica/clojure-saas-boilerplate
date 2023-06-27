(ns saas.button-cards
  (:require
   [saas.ui.button-shad :refer [button button-variants]]
   ["lucide-react" :refer [ChevronRight Mail Loader2]]
   [saas.ui.util :as u]
   [reagent.core :as r]       ; needed for dc/defcard-rg
   [devcards.core :as dc]))

(dc/defcard "# Welcome to buttons \n The colors of the buttons is mainly `brand`. You can change the brand color from `tailwind.config.js`")


(dc/defcard button-props (u/variants->style-props button-variants))


(dc/defcard-rg primary
  "## Usage: \n

  ```clojure
    [button \"Button\"])\n
        ```
  "

  [:div {:class "mx-auto mt-2 flex max-w-3xl flex-col items-center justify-start space-y-4"}
   [button "Button"]])



(dc/defcard-rg secondary
  "## Usage: \n

  ```clojure
    [button {:variant :secondary} \"Secondary\"]\n
        ```
  "
  [:div
   [:div {:class "mx-auto mt-2 flex max-w-3xl flex-col items-center justify-start space-y-4 sm:flex-row sm:items-end sm:justify-around sm:space-y-0"}
    [button {:variant :secondary} "Secondary"]]])

(dc/defcard-rg destructive
  "## Usage: \n

  ```clojure
    [button {:variant :destructive} \"Destructive\"]\n
        ```
  "
  [:div
   [:div {:class "mx-auto mt-2 flex max-w-3xl flex-col items-center justify-start space-y-4 sm:flex-row sm:items-end sm:justify-around sm:space-y-0"}
    [button {:variant :destructive} "Destructive"]]])

(dc/defcard-rg outline
  "## Usage: \n

  ```clojure
    [button {:variant :outline} \"Outline\"]\n
        ```
  "
  [:div
   [:div {:class "mx-auto mt-2 flex max-w-3xl flex-col items-center justify-start space-y-4 sm:flex-row sm:items-end sm:justify-around sm:space-y-0"}
    [button {:variant :outline} "Outline"]]])

(dc/defcard-rg ghost
  "## Usage: \n

  ```clojure
    [button {:variant :ghost} \"Button\"]\n
        ```
  "
  [:div
   [:div {:class "mx-auto mt-2 flex max-w-3xl flex-col items-center justify-start space-y-4 sm:flex-row sm:items-end sm:justify-around sm:space-y-0"}
    [button {:variant :ghost} "Button"]]])


(dc/defcard-rg link
  "## Usage: \n

  ```clojure
    [button {:variant :link} \"Link\"]\n
        ```
  "
  [:div
   [:div {:class "mx-auto mt-2 flex max-w-3xl flex-col items-center justify-start space-y-4 sm:flex-row sm:items-end sm:justify-around sm:space-y-0"}
    [button {:variant :link} "Link"]]])


(dc/defcard-rg icon
  "## Usage: \n

  ```clojure
  (:require
     [\"lucide-react\" :refer [ChevronRight]])

    [button {:variant :outline :size :icon}
     [:> ChevronRight {:class \"h-4 w-4\"}]]
        ```
  "
  [:div
   [:div {:class "mx-auto mt-2 flex max-w-3xl flex-col items-center justify-start space-y-4 sm:flex-row sm:items-end sm:justify-around sm:space-y-0"}
    [button {:variant :outline :size :icon}
     [:> ChevronRight {:class "h-4 w-4"}]]]])


(dc/defcard-rg with-icon
  "## Usage: \n

  ```clojure\n  (:require
      [\"lucide-react\" :refer [Mail]])

      [button [:> Mail {:class \"mr-2 h-4 w-4\"}] \"Login with email\"]
  ```
  "
  [:div
   [:div {:class "mx-auto mt-2 flex max-w-3xl flex-col items-center justify-start space-y-4 sm:flex-row sm:items-end sm:justify-around sm:space-y-0"}
    [button [:> Mail {:class "mr-2 h-4 w-4"}] "Login with email"]]])

(dc/defcard-rg loading
  "## Usage: \n

  ```clojure\n  (:require
      [\"lucide-react\" :refer [Loader2]])

      [button
      [:> Loader2 {:class \"mr-2 h-4 w-4 animate-spin\"}] \"Please wait\"]
  ```
  "
  [:div
   [:div {:class "mx-auto mt-2 flex max-w-3xl flex-col items-center justify-start space-y-4 sm:flex-row sm:items-end sm:justify-around sm:space-y-0"}
    [button {:disabled true}
     [:> Loader2 {:class "mr-2 h-4 w-4 animate-spin"}] "Please wait"]]])


(dc/defcard-rg as-child
  "## Usage: \n

  ```clojure\n  (:require
      [\"lucide-react\" :refer [Loader2]])

      [button
      [:> Loader2 {:class \"mr-2 h-4 w-4 animate-spin\"}] \"Please wait\"]
  ```
  "
  [:div
   [:div {:class "mx-auto mt-2 flex max-w-3xl flex-col items-center justify-start space-y-4 sm:flex-row sm:items-end sm:justify-around sm:space-y-0"}
    [button {:as-child true}
     [:a {:href "login"} "Login"]]]])
