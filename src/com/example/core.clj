(ns com.example.core
  (:require [malli.core :as ma]
            [muotti.core :as muotti]
            [muotti.malli :as mm]))

(def malli-transformer (mm/transformer (muotti/->transformer mm/malli-config)))

(defn parse&coerce
  {:malli/schema [:=>
                  [:cat [:* :any]]
                  [:map-of :keyword :string]]}
  [& args]
  (->> args
       (ma/parse [:* [:cat :any :any]])
       (map #(ma/coerce [:tuple :keyword :string]
                        %
                        malli-transformer))
       (into {})))

^:rct/test
(comment
  (parse&coerce :jar-name 'user.jar)
  ;;=> {:jar-name "user.jar"}
  (parse&coerce :jar-name "user.jar" :version 15)
  ;;=> {:jar-name "user.jar", :version "15"}
  (parse&coerce "jar-name" 'user.jar :version '15)
  ;;=> {:jar-name "user.jar", :version "15"}
  (parse&coerce 'jar-name 'user.jar :version "15")
  ;;=> {:jar-name "user.jar", :version "15"}
  )

;; left those below so instrumentation and tests are run on ns reload
;; but usually I run them manually via calva.customREPLCommandSnippets
((requiring-resolve 'malli.dev/start!))
((requiring-resolve 'com.mjdowney.rich-comment-tests/run-ns-tests!) *ns*)
