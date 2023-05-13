(ns saas.openai.openai
  (:require
   [clojure.java.io :as io]
   [integrant.core :as ig]
   [martian.hato :as martian-hato]
   [martian.core :as martian]
   [martian.openapi :as openapi]
   [martian.yaml :as yaml]
   [saas.openai.sse :as sse]
   [martian.encoders :as encoders]
   [martian.interceptors :as interceptors]
   [schema.core :as s]))

(defn add-headers
  [{:keys [organization api-key]}]
  {:name ::add-headers
   :enter (fn [ctx]
            (let [api-key (or (-> ctx :params :saas.openai.core/options :api-key)
                              api-key)
                  organization (or (-> ctx :params :saas.openai.core/options :organization)
                                   organization)]
              (update-in ctx [:request :headers]
                         (fn [headers]
                           (cond-> headers
                                   (not-empty api-key) (assoc "Authorization" (str "Bearer " api-key))
                                   (not-empty organization) (assoc "OpenAI-Organization" organization))))))})



(defn- multipart-form-data?
  [handler]
  (-> handler :openapi-definition :requestBody :content :multipart/form-data))

(defn- param->multipart-entry
  [[param content]]
  {:name (name param)
   :content (if (or (instance? java.io.File content)
                    (instance? java.io.InputStream content)
                    (bytes? content))
              content
              (str content))})

(def multipart-form-data
  {:name ::multipart-form-data
   :enter (fn [{:keys [handler params] :as ctx}]
            (if (multipart-form-data? handler)
              (-> (assoc-in ctx [:request :multipart]
                            (map param->multipart-entry params))
                  (update-in [:request :headers] dissoc "Content-Type")
                  (update :request dissoc :body))
              ctx))})

(defn update-file-schema
  [m operation-id field-name]
  (martian/update-handler m operation-id assoc-in [:body-schema :body field-name] java.io.File))

(defn update-file-schemas
  [m]
  (-> m
      (update-file-schema :create-transcription :file)
      (update-file-schema :create-translation :file)
      (update-file-schema :create-file :file)
      (update-file-schema :create-image-edit :image)
      (update-file-schema :create-image-edit (schema.core/optional-key :mask))
      (update-file-schema :create-image-variation :image)))


(defn bootstrap-openapi
  "Bootstrap the martian from a local copy of the openai swagger spec.
  Params:
  - openai-config: a map with the following keys:
    - organization: the organization id
    - api-key: the api key
  "
  [openai-config]
  (let [definition (yaml/yaml->edn (slurp (io/resource "openapi.yaml")))
        base-url (openapi/base-url nil nil definition)
        encoders (assoc (encoders/default-encoders)
                   "multipart/form-data" nil)
        opts (update martian-hato/default-opts
                     :interceptors (fn [interceptors]
                                     (-> (remove #(#{:martian.hato/perform-request} (:name %))
                                                 interceptors)
                                         (concat [(add-headers openai-config)
                                                  (interceptors/encode-body encoders)
                                                  multipart-form-data
                                                  sse/perform-sse-capable-request]))))]
    (-> (martian/bootstrap-openapi base-url definition opts)
        update-file-schemas)))


(defmethod ig/init-key :saas/openai
  [_ config]
  (println "Initializing openai client \n")
  (bootstrap-openapi config))
