(ns arx.geom
  (:require [quil.core :as q]))


;; Stuff for showing simple box-like buildings:

(def boxes (atom nil))

(defn reset-boxes []  (reset! boxes nil))

(defn add-box [width min-base max-base min-height max-height]
  (let [r (+ 1100 (* (Math/log (- 1 (q/random 1))) (- width)))
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


;; Stuff for crazy shape:

(defn vertex-values []
  (into []
        (take 100
              (repeatedly (fn []
                            (let [pi 3.1415927
                                  o [0 0 2000]
                                  r (rand 1000)
                                  th (rand pi)
                                  ph (rand (* pi 2))
                                  x (* r
                                       (Math/sin th)
                                       (Math/cos ph))
                                  y (* r
                                       (Math/sin th)
                                       (Math/sin ph))
                                  z (* r (Math/cos th))]
                              [(+ x (nth o 0))
                               (+ y (nth o 1))
                               (+ z (nth o 2))]))))))


(def vertex-atom
  (atom (vertex-values)))

(defn vertices [] @vertex-atom)

(defn reset-vertices [] (reset! vertex-atom (vertex-values)))


;; Stuff for toggle-able sphere:

(def do-sphere-atom (atom false))

(defn toggle-sphere []
  (swap! do-sphere-atom not))

(defn do-sphere [] @do-sphere-atom)
