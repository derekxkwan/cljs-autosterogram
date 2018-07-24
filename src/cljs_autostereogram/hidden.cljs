(ns cljs-autostereogram.hidden
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def bg-color 165)
(def font-sz 100)
(def text-ld 10)
(def cur-height 100)
(def cur-dim {:w nil :h nil})
(def strs ["MODEL MINORITY"])

(defn setup-hidden [w h]
  (set! cur-dim (merge cur-dim {:w w :h h}))
  (let* [want-font (get (q/available-fonts) 0)
         cur-font (q/load-font want-font)
         new-gr (q/create-graphics w h :p2d)]
    {:font cur-font :hidden-gfx new-gr}
    )
  )
    
(defn draw-hidden []
  (q/background bg-color)
  (q/text-leading text-ld)
  (q/fill 255)
  (q/text-size font-sz)
  (let* [w (get cur-dim :w)
         h (get cur-dim :h)
         cur-str (get strs 0)
         cur-width (q/text-width cur-str)
         x1 (- (/ w 2.0) (/ cur-width 2.0))
         x2 (+ (/ w 2.0) (/ cur-width 2.0))
         y1 (- (/ h 2.0) (/ cur-height 2.0))
         y2 (+ (/ h 2.0) (/ cur-height 2.0))]
    (q/text cur-str x1 y1 x2 y2))
  )
         
  