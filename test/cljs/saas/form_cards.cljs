(ns saas.form-cards
  (:require [clojure.test :refer [deftest is testing]]
            [saas.ui.forms :refer [input label form-control]]
            [reagent.core :as r] ; needed for dc/defcard-rg
            [devcards.core :as dc]))

(dc/defcard-rg input
  "## Usage: \n

  ```clojure
    [ui/input {:id \"email\" :name \"email\" :type \"email\" :auto-complete \"email\" :required true}]\n
        ```
  "
  [:div
   [:div {:class "mx-auto flex max-w-3xl flex-col items-center justify-start space-y-4 sm:flex-row sm:items-end sm:justify-around sm:space-y-0"}
    [input {:id "email" :name "email" :type "email" :auto-complete "email" :required true
               :placeholder "test@example.com"
               :class "mt-2"}]]
   ])

(dc/defcard-rg input-with-label
  "## Usage: \n

  ```clojure
   [:div {:class \"mx-auto max-w-sm space-y-4 sm:space-y-0\"}\n    [ui/label {:for \"email\"} \"Email\"]\n    [ui/input {:id \"email\" :name \"email\" :type \"email\" :auto-complete \"email\" :required true\n               :class \"mt-2\"}]]\n
        ```
  "
  [:div
   [:div {:class "mx-auto max-w-sm space-y-4 sm:space-y-0 mt-2"}
    [label {:for "email2"} "Email"]
    [input {:id "email2" :name "email" :type "email" :auto-complete "email" :required true
               :placeholder "test@example.com"
               :class "mt-2"}]]
   ])

(dc/defcard-rg input-with-label
  "## Usage: \n

  ```clojure
   [:div {:class \"mx-auto max-w-sm space-y-4 sm:space-y-0\"}\n    [ui/label {:for \"email\"} \"Email\"]\n    [ui/input {:id \"email\" :name \"email\" :type \"email\" :auto-complete \"email\" :required true\n               :class \"mt-2\"}]]\n
        ```
  "
  [:div
   [:div {:class "mx-auto max-w-sm space-y-4 sm:space-y-0 mt-2"}
    [label {:for "email2"} "Email"]
    [input {:id "email2" :name "email" :type "email" :auto-complete "email" :required true
               :placeholder "test@example.com"
               :class "mt-2"}]]
   ])

(dc/defcard-rg form-control
  "## Usage: \n

  ```clojure
   [:div {:class \"mx-auto max-w-sm space-y-4 sm:space-y-0\"}\n    [ui/label {:for \"email\"} \"Email\"]\n    [ui/input {:id \"email\" :name \"email\" :type \"email\" :auto-complete \"email\" :required true\n               :class \"mt-2\"}]]\n
        ```
  "
  [:div
   [form-control
    [label {:for "id"} "ID"]
    [input {:id "id" :name "ID" :type "text" :placeholder "THIS IS ID"} "ID"]
    ]
   ])
