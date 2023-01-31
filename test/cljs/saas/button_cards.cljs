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
      [ui/button {:label "Button text" :size size}]
      )]
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
   [:div {:class "mx-auto flex max-w-3xl flex-col items-center justify-start space-y-4 sm:flex-row sm:items-end sm:justify-around sm:space-y-0"}
    (for [size [:xs :sm :md :lg :xl]]
      [ui/button {:label "Button text" :type :secondary :size size}]
      )]
   ])
