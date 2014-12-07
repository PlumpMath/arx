(defproject eigenhombre/arx "0.0.0"
  :description "Experiments in 3d design."
  :url "https://github.com/eigenhombre/arx/"
  :main arx.core
  :aot [arx.core]
  :aliases {"autotest" ["midje" ":autotest"]}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [quil/quil "2.2.4"]
                 [midje "1.6.3"]
                 [net.mikera/core.matrix "0.31.1"]]
  :profiles {:dev {:plugins [[lein-midje "3.0.0"]]}})
