(ns arx.geom)


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
