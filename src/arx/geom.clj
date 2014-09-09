(ns arx.geom
  (:require [quil.core :as q]))


(defn num-deltas [a b]
  (apply + (for [coord (range 3)
                 :when (not= (Math/abs (- (get a coord)
                                          (get b coord)))
                             0)]
             1)))


(defn square-lines []
  (let [corners (for [x [-100 200]
                      y [-100 200]
                      z [-100 200]]
                  [x y z])]
    (for [c1 corners
          c2 corners
          :when (and (not= c1 c2)
                     (= (num-deltas c1 c2) 1))]
      (concat c1 c2))))


(def boxes (atom nil))


(defn reset-boxes []  (reset! boxes nil))


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


(defn add-enough-boxes []
  (while (or (< (count @boxes) 1000)
             (< (q/random 1) 0.03))
    (add-either-box)))


(defn box-seq [] @boxes)
