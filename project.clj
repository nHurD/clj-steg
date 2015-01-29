(defproject clj-steg "0.1.0-SNAPSHOT"
  :description "A simple steganographic application"
  :url "https://github.com/nHurD/clj-steg"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
f  :main ^:skip-aot clj-steg.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
