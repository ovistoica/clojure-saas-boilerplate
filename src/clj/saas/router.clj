(ns saas.router
  (:require [reitit.ring :as ring]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [ring.util.response :as rr]
            [muuntaja.core :as m]
            [reitit.coercion.malli :as coercion-malli]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.coercion.spec :as coercion-spec]
            [reitit.ring.coercion :as coercion]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.spec :as rs]
            [reitit.ring.middleware.dev :as dev]
            [ring.middleware.cors :as cors]
            [reitit.exception :as r-exception]
            [saas.middleware :as mw]
            [saas.routes :as s-routes]))



(def swagger-docs
  ["/swagger.json"
   {:get
    {:no-doc true
     :swagger {:basePath "/"
               :info {:title "SaaS API Reference"
                      :description "The SaaS API is organized around REST. Returns JSON, Transit (msgpack, json), or EDN  encoded responses."
                      :version "1.0.0"}}
     :handler (swagger/create-swagger-handler)}}])

(def router-config
  {:validate rs/validate
   ;:reitit.middleware/transform dev/print-request-diffs     ;; This is for debugging purposes
   :exception pretty/exception
   :conflicts (fn [conflicts]
                (println (r-exception/format-exception :path-conflicts nil conflicts)))
   :data {:coercion coercion-malli/coercion
          :muuntaja m/instance
          :middleware [;; swagger feature
                       swagger/swagger-feature
                       ;; query-params & form-params
                       parameters/parameters-middleware
                       ;; content-negotiation
                       muuntaja/format-negotiate-middleware
                       ;; encoding response body
                       muuntaja/format-response-middleware
                       ;; decoding request body
                       muuntaja/format-request-middleware
                       ;; exception handling
                       mw/exception-middleware
                       ;; coercing response bodies
                       coercion/coerce-response-middleware
                       ;; coercing request parameters
                       coercion/coerce-request-middleware
                       ;; multipart
                       multipart/multipart-middleware]}})


(defn cors-middleware
  "Middleware to allow different origins"
  [handler]
  (cors/wrap-cors handler :access-control-allow-origin [#".*"]
                  :access-control-allow-methods [:get :put :post :delete]))

(defn routes
  [config]
  (ring/ring-handler
    (ring/router
      [swagger-docs
       ["/v1"
        (saas.routes/saas-routes config)]]
      router-config)
    (ring/routes
      (swagger-ui/create-swagger-ui-handler {:path "/"})
      ; Finally, if nothing matches, return 404
      (constantly {:status 404, :body ""}))
    {:middleware [cors-middleware]}))
