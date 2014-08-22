(ns arx.core
  (:gen-class)
  (:require [quil.core :as q]
            [arx.geom :refer [square-lines]]))


(defn setup []
  (q/set-state! :t 0
                :phi 0
                :target-phi 0
                :r 4000
                :target-r 4000
                :theta (q/radians 75)
                :target-theta (q/radians 75)
                :paused false))


(def boxes (atom nil))


(defn toggle-paused []
  (swap! (q/state-atom) update-in [:paused] not))


(defn paused [] (:paused (q/state)))
(defn phi [] (:phi (q/state)))
(defn theta [] (:theta (q/state)))
(defn r [] (:r (q/state)))


(defn update [kw f]
  (swap! (q/state-atom) update-in [kw] f))


(defn assign [kw val]
  (swap! (q/state-atom) assoc-in [kw] val))


(defn getk [kw] (get @(q/state-atom) kw))


(defn update-camera []
  (update :phi (partial + (q/radians 0.10)))
  (update :t inc)
  (assign :target-r (+ 4000 (* 2000 (Math/sin (* 0.001 (getk :t))))))
  (assign :target-theta (+ (q/radians 65) (* (q/radians 10)
                                             (Math/sin (* 0.005 (getk :t))))))
  (let [del-theta (* 0.01 (- (getk :target-theta) (getk :theta)))
        del-r (* 0.005 (- (getk :target-r) (getk :r)))]
    (swap! (q/state-atom) update-in [:theta] (partial + del-theta))
    (swap! (q/state-atom) update-in [:r] (partial + del-r))))


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


(defn add-box [width min-base max-base min-height max-height]
  (let [r (* (Math/log (- 1 (q/random 1))) (- width))
        phi (q/random q/TWO-PI)
        x (* r (Math/cos phi))
        y (* r (Math/sin phi))
        zeta (q/random q/TWO-PI)
        base (q/random min-base max-base)
        height (q/random min-height max-height)]
    (swap! boxes conj [x y base height zeta])))


(defn add-wide-box []
  (add-box 1000 0 400 0 100))


(defn add-narrow-box []
  (add-box 4000 100 150 600 700))


(defn add-either-box []
  (if (< (q/random 1) 0.04)
    (add-narrow-box)
    (add-wide-box)))


(defn draw-boxes []
  (doseq [[x y base height zeta] @boxes]
    (q/push-matrix)
    (q/translate x y (/ height 2))
    (q/rotate-z zeta)
    (q/box base base height)
    (q/pop-matrix)))


(defn update-perspective []
  (q/perspective q/THIRD-PI
                 (/ (q/width) (q/height))
                 (/ (:r (q/state)) 10.0)
                 (* (:r (q/state)) 500.0)))

(defn draw []
  (q/lights)
  (q/background 200)
  (draw-axes)
  (when-not (paused)
    (update-camera)
    (update-perspective)
    (while (or (< (count @boxes) 1000)
               (< (q/random 1) 0.03))
      (add-either-box)))
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
  (update-perspective))


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
