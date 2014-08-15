(ns arx.core
  (:gen-class)
  (:require [quil.core :as q]
            [arx.geom :refer [square-lines]]))


(defn setup []
  (q/set-state! :phi 0
                :r 1000
                :theta (q/radians 45)
                :paused false))


(def boxes (atom nil))


(defn toggle-paused []
  (swap! (q/state-atom) update-in [:paused] not))


(defn paused [] (:paused (q/state)))
(defn phi [] (:phi (q/state)))
(defn theta [] (:theta (q/state)))
(defn r [] (:r (q/state)))

(defn update-rotation []
  (swap! (q/state-atom) update-in [:phi] (partial + (q/radians 0.5))))


(defn key-press []
  (if (= (q/raw-key) \c)
    (reset! boxes nil)
    (toggle-paused)))


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


(defn add-random-box []
  (let [width 1000
        r (* (Math/log (- 1 (q/random 1))) (- width))
        phi (q/random q/TWO-PI)
        x (* r (Math/cos phi))
        y (* r (Math/sin phi))
        zeta (q/random q/TWO-PI)
        size (q/random 200)]
    (swap! boxes conj [x y size zeta])))


(defn draw-boxes []
  (doseq [[x y l zeta] @boxes]
    (q/push-matrix)
    (q/translate x y (/ l 2))
    (q/rotate-z zeta)
    (q/box l)
    (q/pop-matrix)))


(defn draw []
  (q/background 200)
  (draw-axes)
  (when-not (paused)
    (update-rotation)
    (when (< (q/random 1) 0.1)
      (add-random-box)))
  (q/camera (* (r) (Math/cos (phi)) (Math/sin (theta)))
            (* (r) (Math/sin (phi)) (Math/sin (theta)))
            (* (r) (Math/cos (theta)))
            0 0 0
            0 0 -1)
  (draw-boxes))


(defn mouse-dragged []
  (let [delx (- (q/mouse-x)
                (q/pmouse-x))
        mdx (/ delx 3)
        dely (- (q/pmouse-y)
                (q/mouse-y))
        mdy (/ dely 3)]
    (swap! (q/state-atom) update-in [:phi] (partial + (q/radians mdx)))
    (swap! (q/state-atom) update-in [:theta] (partial + (q/radians mdy)))))


(defn mouse-wheel [amount]
  (swap! (q/state-atom) update-in [:r] #(max 1 (+ % (* 7 amount))))
  (q/perspective q/THIRD-PI
                 (/ (q/width) (q/height))
                 (/ (:r (q/state)) 10.0)
                 (* (:r (q/state)) 100.0)))


(defn -main []
  (q/defsketch x
    :title ""
    :setup setup
    :draw draw
    :size [1600 1000]
    :key-typed key-press
    :mouse-dragged mouse-dragged
    :mouse-wheel mouse-wheel
    :renderer :opengl))
