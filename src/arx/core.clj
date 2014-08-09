(ns arx.core
  (:gen-class)
  (:require [quil.core :as q]))


(defn draw []
  (q/ellipse 400 500 400 500))


(q/defsketch x
  :title ""
  :draw draw
  :size [800 1000])


(defn -main [])
