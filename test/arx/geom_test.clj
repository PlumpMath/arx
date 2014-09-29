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


(defn- extrude [face amt]
  (let [paths (all-face-paths face)
        points []
        dir (extrude-direction points)
        vdir (o/* dir amt)]
    (concat
     (for [[p1 p2]
           (take (count points)
                 (partition 2 1 (cycle points)))]
       [p1 p2 (o/+ vdir p2) (o/+ vdir p1)])
     [points
      (map (partial o/+ vdir) points)])))


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
      (->> p1
           face
           extrude-direction
           (map #(Math/abs %))) => [0.0 0.0 1.0])

    (fact                           "I can extrude a face, turning all
                                     edges into faces and adding a
                                     'top' face."
      ;; (-> p1
      ;;     face
      ;;     (extrude 3))
      )))




;; WIP: finish adapting these to point/edge/path/face model:


(defn- split-edge [[p1 p2] amt]
  (let [del (o/* (o/- p2 p1) amt)]
    [(o/+ p1 del)
     (o/- p2 del)]))


;; #_((defn- split-rect [face edge-index-0 edge-index-1 amt0 amt1])
;;  [[0 0 0] [1 0 0] [1 1 0] [0 1 0]])


(comment
  (let [base [[0 0 0]
              [0 1000 0]
              [1000 1000 0]
              [1000 0 0]]
        cube (extrude base 1000)]
    cube)

  ;;=>
  [[[0 0 0] [0 1000 0] [0.0 1000.0 1000.0] [0.0 0.0 1000.0]]
   [[0 1000 0] [1000 1000 0] [1000.0 1000.0 1000.0] [0.0 1000.0 1000.0]]
   [[1000 1000 0] [1000 0 0] [1000.0 0.0 1000.0] [1000.0 1000.0 1000.0]]
   [[1000 0 0] [0 0 0] [0.0 0.0 1000.0] [1000.0 0.0 1000.0]]
   [[0 0 0] [0 1000 0] [1000 1000 0] [1000 0 0]]
   [[0.0 0.0 1000.0]
    [0.0 1000.0 1000.0]
    [1000.0 1000.0 1000.0]
    [1000.0 0.0 1000.0]]])
