(ns saas.button-cards
  (:require
    [saas.ui :as ui]
    [reagent.core :as r]      ; needed for dc/defcard-rg
    [devcards.core :as dc]))

(dc/defcard "# Welcome to buttons \n The colors of the buttons is mainly `brand`. You can change the brand color from `tailwind.config.js`")



(dc/defcard button-props (ui/variants->style-props ui/button-variants))



(dc/defcard-rg type-primary
  "## Usage: \n

  ```clojure
  ;; :type :primary is the default
  (for [size [:xs :sm :md :lg :xl]]
    [ui/button {:label \"Button text\" :size size}])\n
        ```
  "

  [:div
   [:div {:class "mx-auto flex max-w-3xl flex-col items-center justify-start space-y-4 sm:flex-row sm:items-end sm:justify-around sm:space-y-0"}
    (for [size [:xs :sm :md :lg :xl]]
      [ui/button {:size size} "Label"])]
   ])

(dc/defcard-rg type-secondary
  "## Usage: \n

  ```clojure
  (for [size [:xs :sm :md :lg :xl]]
    [ui/button {:label \"Button text\"
                :type :secondary
                :size size }])\n
        ```
  "
  [:div
   [:div {:class "mx-auto mt-2 flex max-w-3xl flex-col items-center justify-start space-y-4 sm:flex-row sm:items-end sm:justify-around sm:space-y-0"}
    (for [size [:xs :sm :md :lg :xl]]
      ^{:key (str size)}
      [ui/button {:type :secondary :size size} "Label"])]
   ])

(dc/defcard-rg loading-button
  "## Usage: \n

  ```clojure
  (for [size [:xs :sm :md :lg :xl]]
    [ui/button {:label \"Button text\"
                :size size
                :loading? true}])\n
        ```
  "
  [:div
   [:div {:class "mx-auto flex max-w-3xl flex-col items-center justify-start space-y-4 sm:flex-row sm:items-end sm:justify-around sm:space-y-0"}
    (for [size [:xs :sm :md :lg :xl]]
      [ui/button {:size size
                  :loading? true}])]
   [:div {:class "mx-auto my-2 flex max-w-3xl flex-col items-center justify-start space-y-4 sm:flex-row sm:items-end sm:justify-around sm:space-y-0"}
    (for [size [:xs :sm :md :lg :xl]]
      [ui/button {:type :secondary
                  :size size
                  :loading? true}])]])

(dc/defcard-rg disabled-button
  "## Usage: \n

  ```clojure
  (for [size [:xs :sm :md :lg :xl]]
    [ui/button {:size size
                :disabled? true}])\n
        ```
  "
  [:div
   [:div {:class "mx-auto flex max-w-3xl flex-col items-center justify-start space-y-4 sm:flex-row sm:items-end sm:justify-around sm:space-y-0"}
    (for [size [:xs :sm :md :lg :xl]]
      [ui/button {:size size
                  :loading? true}])]
   [:div {:class "mx-auto my-2 flex max-w-3xl flex-col items-center justify-start space-y-4 sm:flex-row sm:items-end sm:justify-around sm:space-y-0"}
    (for [size [:xs :sm :md :lg :xl]]
      [ui/button {:type :secondary
                  :size size
                  :loading? true}])]])

(dc/defcard-rg btn
  [:div {:class "mx-auto flex max-w-3xl flex-col items-center justify-start space-y-4 sm:flex-row sm:items-end sm:justify-around sm:space-y-0"}
   (for [size [:xs :sm :md :lg :xl]]
     [ui/button {:size size}
      [:p.font-bold.italic (str "Button " size)]])])
