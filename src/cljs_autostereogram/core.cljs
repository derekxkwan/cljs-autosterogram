(ns cljs-autostereogram.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [cljs-autostereogram.hidden :as h]
            [cljs-autostereogram.sgram :as a]))

(def cur-dim {:w 1024 :h 768})
(def scaling 4)

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.

  ; setup function returns initial state. It contains
  ; circle color and position.

  (let [cur-state (atom {:debug false})
        w (get cur-dim :w)
        h (get cur-dim :h)]
    (swap! cur-state merge (h/setup-hidden w h scaling))
    (swap! cur-state merge {:sgram (a/setup-sgram (get h/cur-dim :w) (get h/cur-dim :h))})
    )
  )

(defn update-state [state]
  (merge state {:placeholder true})
  )

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 240)
  (let [hidden-gfx (get state :hidden-gfx)
        sgram (get state :sgram)
        cur-w (get h/cur-dim :w)
        cur-h (get h/cur-dim :h)
        cnv-w (get cur-dim :w)
        cnv-h (get cur-dim :h)
        debug? (get state :debug)]
    (q/with-graphics hidden-gfx
      (h/draw-hidden))
    (q/with-graphics sgram
      (a/draw-sgram hidden-gfx cur-w cur-h)
      )

    (if (true? debug?)
      (q/image hidden-gfx 0 0 cnv-w cnv-h)
      (q/image sgram 0 0 cnv-w cnv-h)
      )

    )

  )

(defn mouse-pressed [state evt]
 (merge state {:debug true})
  )

(defn mouse-released [state evt]
  (merge state {:debug false})
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
    :mouse-pressed mouse-pressed
    :mouse-released mouse-released
    :middleware [m/fun-mode]))

; uncomment this line to reset the sketch:
(run-sketch)
