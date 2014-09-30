(ns arx.geom-test
  (:require [midje.sweet :refer :all]
            [clojure.core.matrix :as m]
            [clojure.core.matrix.operators :as o]))


(defn point [x y z] (vector x y z))
(defn edge [e1 e2] (vector e1 e2))
(defn path [& edges] (assert (< 2 (count edges))) edges)
(defn path-points [p] (apply concat p))
(defn face [outer & inners] (list* outer inners))
(defn all-face-paths [f] f)
(defn face-boundary [f] (first f))


(defn- extrude-direction [f]
  (let [[[e00 e01] [_ e11]] (face-boundary f)
        v1 (o/- e01 e00)
        v2 (o/- e11 e01)]
    (m/normalise (m/cross v2 v1))))


(defn- extrude [f amt]
  (let [dir (map #(Math/abs %) (extrude-direction f))
        vdir (o/* dir amt)]
    (conj (for [pth (all-face-paths f)
                [p1 p2] pth]
            (face (path (edge p1 p2)
                        (edge p2 (o/+ vdir p2))
                        (edge (o/+ vdir p2) (o/+ vdir p1))
                        (edge (o/+ vdir p1) p1))))
          f f)))


(fact                               "I can write long and interesting
                                     facts on the side margin, making
                                     things easier to read.")

(fact                               "I can create a point at the origin"
  (point 0 0 0) =not=> nil)

(fact                               "An edge consists of two or more
                                     points."
  (edge) => (throws)
  (edge (point 0 0 0)) => (throws)
  (edge (point 0 0 0)
        (point 0 0 1)) =not=> (throws))

(fact                               "A path is a series of three or more edges,
                                     with endpoints joined."
  (path) => (throws)
  (path (edge (point 0 0 0)
              (point 1 0 0))) => (throws)
  (path (edge (point 0 0 0)
              (point 1 0 0))
        (edge (point 1 0 0)
              (point 0 0 0))) => (throws)
  (let [pp (path-points
            (path (edge (point 0 0 0)
                        (point 1 0 0))
                  (edge (point 1 0 0)
                        (point 1 1 0))
                  (edge (point 1 1 0)
                        (point 0 0 0))))]
    pp =not=> nil
    (first pp) =not=> nil
    (last pp) =not=> nil
    (first pp) => (last pp)))


(fact                               "A face consists of an outer path and
                                     zero or more inner ('hole')
                                     paths."
  (face) => (throws)
  (let [p1 (path (edge (point 0 0 0)
                       (point 1 0 0))
                 (edge (point 1 0 0)
                       (point 1 1 0))
                 (edge (point 1 1 0)
                       (point 0 0 0)))
        p2 (path (edge (point 0.1 0.1 0)
                       (point 0.9 0.1 0))
                 (edge (point 0.9 0.1 0)
                       (point 0.9 0.9 0))
                 (edge (point 0.9 0.9 0)
                       (point 0.1 0.1 0)))]
    (->> (face p1)
         face-boundary
         count) => 3
    (->> (face p1 p2)
         face-boundary
         count) => 3

    (fact                           "I can get the correct normal to a
                                     horizontal face."
      (->> (face p1)
           extrude-direction
           (map #(Math/abs %))) => [0.0 0.0 1.0])

    (fact                           "I can extrude a face, turning all
                                     edges into faces and adding a
                                     'top' face to the side and bottom faces."
      (-> (face p1)
          (extrude 3)
          count) => 5)

    (fact                           "I can extrude a face with a hole and
                                     get the right number of faces."
      (-> (face p1 p2)
          (extrude 3)
          count) => 8)

    (future-fact                    "Top and bottom faces after extraction
                                     all have the right z coordinate.")))
