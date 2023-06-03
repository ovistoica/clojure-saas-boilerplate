(ns migrations
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [saas.config :refer [migrations-config]])
  (:import (java.io File)
           (java.net URL URLDecoder)
           (java.text SimpleDateFormat)
           (java.util Date TimeZone)
           (java.util.jar JarFile)))


(def default-migration-parent "resources/")
(def default-migration-dir "migrations")
(def default-init-script-name "init.sql")

(defn ->kebab-case [s]
  (-> (reduce
        (fn [s c]
          (if (and
                (not-empty s)
                (Character/isLowerCase (char (last s)))
                (Character/isUpperCase (char c)))
            (str s "-" c)
            (str s c)))
        "" s)
      (str/replace #"[\s]+" "-")
      (.replaceAll "_" "-")
      (.toLowerCase)))

(defn- timestamp []
  (let [fmt (doto (SimpleDateFormat. "yyyyMMddHHmmss ")
              (.setTimeZone (TimeZone/getTimeZone "UTC")))]
    (.format fmt (Date.))))

(defn jar-file [^URL url]
  (some-> url
          (.getFile)
          (URLDecoder/decode "UTF-8")
          (.split "!/")
          ^String (first)
          (.replaceFirst "file:" "")
          (JarFile.)))

(defn get-parent-migration-dir
  "Gets the :parent-migration-dir from config, or default if missing."
  [config]
  (get config :parent-migration-dir default-migration-parent))

(defn get-migration-dir
  "Gets the :migration-dir from config, or default if missing."
  [config]
  (get config :migration-dir default-migration-dir))



(defn find-migration-dir
  "Finds the given directory on the classpath. For backward
  compatibility, tries the System ClassLoader first, but falls back to
  using the Context ClassLoader like Clojure's compiler.
  If classloaders return nothing try to find it on a filesystem."
  ([^String dir]
   (or (find-migration-dir (ClassLoader/getSystemClassLoader) default-migration-parent dir)
       (-> (Thread/currentThread)
           (.getContextClassLoader)
           (find-migration-dir default-migration-parent dir))))
  ([^ClassLoader class-loader ^String parent-dir ^String dir]
   (if-let [^URL url (.getResource class-loader dir)]
     (if (= "jar" (.getProtocol url))
       (jar-file url)
       (File. (URLDecoder/decode (.getFile url) "UTF-8")))
     (let [migration-dir (io/file parent-dir dir)]
       (if (.exists migration-dir)
         migration-dir
         (let [no-implicit-parent-dir (io/file dir)]
           (when (.exists no-implicit-parent-dir)
             no-implicit-parent-dir)))))))

(defn migration-name
  [name]
  (->kebab-case (str (timestamp) name)))

(defmulti get-extension*
  "Dispatcher to get the supported file extension for this migration"
  (fn [mig-type]
    mig-type))

(defmethod get-extension* :sql
  [_]
  "sql")

(defmulti migration-files*
  "Dispatcher to get a list of filenames to create when creating new migrations"
  (fn [mig-type migration-name]
    mig-type))

(defmethod migration-files* :default
  [mig-type migration-name]
  (throw (Exception. (format "Unknown migration type '%s'"
                             (clojure.core/name mig-type)))))

(defmethod migration-files* :sql
  [x migration-name]
  (let [ext (get-extension* x)]
    [(str migration-name ".up." ext)
     (str migration-name ".down." ext)]))


(defn find-or-create-migration-dir
  ([dir] (find-or-create-migration-dir default-migration-parent dir))
  ([parent-dir dir]
   (if-let [migration-dir (find-migration-dir dir)]
     migration-dir

     ;; Couldn't find the migration dir, create it
     (let [new-migration-dir (io/file parent-dir dir)]
       (io/make-parents new-migration-dir ".")
       new-migration-dir))))

(defn create* [config name migration-type]
  (let [migration-dir (find-or-create-migration-dir
                        (get-parent-migration-dir config)
                        (get-migration-dir config))
        migration-name (->kebab-case (str (timestamp) name))]
    (doseq [mig-file (migration-files* migration-type migration-name)]
      (.createNewFile (io/file migration-dir mig-file)))))

(defn create-migration
  [name migration-type]
  (create* (migrations-config) name migration-type))


(comment

  (create-migration "calories_user_id_not_unique" :sql))
