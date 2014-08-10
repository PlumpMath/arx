(ns arx.core
  (:gen-class)
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


(defn draw []
  (q/translate (/ (q/width) 2) (/ (q/height) 2) 0)
  (q/rotate-y (* (q/frame-count) 0.03))
  (q/rotate-x (* (q/frame-count) 0.04))
  (q/ellipse -300 -300 400 500)
  (doall (map (partial apply q/line)
              (square-lines))))


(defn -main []
  (q/defsketch x
    :title ""
    :draw draw
    :size [800 1000]
    :renderer :opengl))  ;; <- translate origin works
