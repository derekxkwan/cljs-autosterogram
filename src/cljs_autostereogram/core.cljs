(ns cljs-autostereogram.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [cljs-autostereogram.hidden :as h]
            [cljs-autostereogram.autostereogram :as a]))

(def cur-dim {:w 1024 :h 768})

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; setup function returns initial state. It contains
  ; circle color and position.

  (let [cur-state {:placeholder true}]
    (merge cur-state (h/setup-hidden (get cur-dim :w) (get cur-dim :h)))
    )
  )

(defn update-state [state]
  (merge state {:placeholder true})
  )

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 240)
  (let [hidden-gfx (get state :hidden-gfx)]
    (q/with-graphics hidden-gfx
      (h/draw-hidden))
    (q/image hidden-gfx 0 0))

  )


; this function is called in index.html
(defn ^:export run-sketch []
  (q/defsketch cljs-autostereogram
    :host "cljs-autostereogram"
    :size [(get cur-dim :w) (get cur-dim :h)]
    ; setup function called only once, during sketch initialization.
    :setup setup
    ; update-state is called on each iteration before draw-state.
    :update update-state
    :draw draw-state
    ; This sketch uses functional-mode middleware.
    ; Check quil wiki for more info about middlewares and particularly
    ; fun-mode.
    :middleware [m/fun-mode]))

; uncomment this line to reset the sketch:
(run-sketch)
