(ns com.example.core
  (:require [malli.core :as ma]
            [muotti.core :as muotti]
            [muotti.malli :as mm]
            [hyperfiddle.rcf :as rcf]))

(def malli-transformer (mm/transformer (muotti/->transformer mm/malli-config)))

(defn parse&coerce
  [& args]
  (->> args
       (ma/parse [:* [:cat :any :any]])
       (map #(ma/coerce [:tuple :keyword :string]
                        %
                        malli-transformer))
       (into {})))

(rcf/enable!)

(rcf/tests
 (parse&coerce :jar-name 'user.jar)               := {:jar-name "user.jar"}
 (parse&coerce :jar-name "user.jar" :version 15)  := {:jar-name "user.jar", :version "15"}
 (parse&coerce "jar-name" 'user.jar :version '15) := {:jar-name "user.jar", :version "15"}
 (parse&coerce 'jar-name 'user.jar :version "15") := {:jar-name "user.jar", :version "15"})
