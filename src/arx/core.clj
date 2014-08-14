(ns arx.core
  (:gen-class)
  (:require [quil.core :as q]
            [arx.geom :refer [square-lines]]))


(defn setup []
  (q/set-state! :phi 0
                :theta (q/radians 45)
                :paused false))


(defn toggle-paused []
  (swap! (q/state-atom) update-in [:paused] not))


(defn paused [] (:paused (q/state)))
(defn phi [] (:phi (q/state)))
(defn theta [] (:theta (q/state)))
(defn update-rotation []
  (swap! (q/state-atom) update-in [:phi] (partial + (q/radians 0.5))))


(defn key-press []
  (toggle-paused))


(defn draw-axes []
  (let [inf 100000]
    ;; x
    (q/stroke 255 0 0)
    (q/line 0 0 0 inf 0 0)
    ;; y
    (q/stroke 0 255 0)
    (q/line 0 0 0 0 inf 0)
    ;; z
    (q/stroke 0 0 255)
    (q/line 0 0 0 0 0 inf)
    (q/stroke 0)))


(defn draw []
  (if-not (paused) (update-rotation))
  (q/background 200)
  (q/translate (/ (q/width) 2) (/ (q/height) 2) 0)
  (q/camera (* 1000 (Math/cos (phi)) (Math/sin (theta)))
            (* 1000 (Math/sin (phi)) (Math/sin (theta)))
            (* 1000 (Math/cos (theta)))
            0 0 0
            0 0 -1)
  (draw-axes)
  (q/box 50)
  (doall (map (partial apply q/line)
              (square-lines))))


(defn mouse-dragged []
  (let [delx (- (q/mouse-x)
                (q/pmouse-x))
        mdx (/ delx 3)
        dely (- (q/pmouse-y)
                (q/mouse-y))
        mdy (/ dely 3)]
    (swap! (q/state-atom) update-in [:phi] (partial + (q/radians mdx)))
    (swap! (q/state-atom) update-in [:theta] (partial + (q/radians mdy)))))


(defn -main []
  (q/defsketch x
    :title ""
    :setup setup
    :draw draw
    :size [800 1000]
    :key-typed key-press
    :mouse-dragged mouse-dragged
    :renderer :opengl))
