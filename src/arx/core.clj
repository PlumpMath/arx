(ns arx.core
  (:gen-class)
  (:require [quil.core :as q]
            [arx.geom :refer [square-lines]]))


(defn setup []
  (q/set-state! :rotation 0
                :paused false))


(defn toggle-paused []
  (swap! (q/state-atom) update-in [:paused] not))


(defn paused [] (:paused (q/state)))
(defn rotation [] (:rotation (q/state)))
(defn update-rotation [] (swap! (q/state-atom) update-in [:rotation] inc))


(defn key-press []
  (toggle-paused))


(defn draw []
  (if-not (paused) (update-rotation))
  (q/background 200)
  (q/translate (/ (q/width) 2) (/ (q/height) 2) 0)
  (q/rotate-y (* (rotation) 0.01))
  (q/rotate-x (* (rotation) 0.02))
  (doall (map (partial apply q/line)
              (square-lines))))


(defn -main []
  (q/defsketch x
    :title ""
    :setup setup
    :draw draw
    :size [800 1000]
    :key-typed key-press
    :renderer :opengl))  ;; <- translate origin works
