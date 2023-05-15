(ns build
  (:require [clojure.tools.build.api :as b]))

(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def uber-file "target/app-standalone.jar")

(defn clean [_]
  (b/delete {:path "target"}))

(defn uber [_]
  (println "Cleaning directory...")
  (clean nil)
  (println "Copying source files...")
  (b/copy-dir {:src-dirs ["src/clj" "resources"]
               :target-dir class-dir})

  (println "Compiling source files...")
  (b/compile-clj {:basis basis
                  :src-dirs ["src/clj"]
                  :class-dir class-dir})

  (println "Building uberjar...")
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis basis
           :main 'saas.server})
  (println "Done!"))