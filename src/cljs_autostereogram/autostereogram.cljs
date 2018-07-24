(ns cljs-autostereogram.autostereogram
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            ))

(def cur-dim {:w nil :h nil})
(def dpi 191.0)
(def mu (/ 1 3.0))
(def scaling 4.0)
(def conv_rad 20) ;;convergence circle radius
(def conv_ypos (/ 3.0 4.0))
(def base-color nil) ;; normalized coor
(def sum-thresh 2.25) ;;threshold components of base-color should sum up to
(def e nil)

(defn calculate-e []
  (Math/round (/ (* 2.5 dpi) scaling))
  )

(defn randomize-color []
  (let [rand-color (atom [0 0 0])]
    (while (<= (reduce + @rand-color) sum-thresh)
      (swap! rand-color (fn [] (repeatedly 3 #(rand 1.0)))))
    @rand-color
    ))

(defn separation [z]
  (Math/round (/ (- 1.0 (* mu z)) (- 2.0 (* mu z))))
  )

(defn setup-sgram [w h]
  (set! cur-dim (merge cur-dim {:w w :h h}))
  (set! base-color (randomize-color))
  (set! e (calculate-e))
  (let [new-gr (q/create-graphics w h :p2d)]
    {:sgram-gfx new-gr})
  )
