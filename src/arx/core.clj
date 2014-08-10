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


(defn draw-axes []
  (let [inf 100000]
    (q/stroke 255 0 0)
    (q/line 0 0 0 inf 0 0)
    (q/stroke 0 255 0)
    (q/line 0 0 0 0 inf 0)
    (q/stroke 0 0 255)
    (q/line 0 0 0 0 0 inf)
    (q/stroke 0)))


(defn draw []
  (if-not (paused) (update-rotation))
  (q/background 200)
  (q/translate (/ (q/width) 2) (/ (q/height) 2) 0)
  (q/rotate-y (* (rotation) 0.01))
  (q/rotate-x (* (rotation) 0.02))
  ;(q/sphere 100)
  (draw-axes)
  (q/box 50)
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
