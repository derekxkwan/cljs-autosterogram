(ns cljs-autostereogram.hidden
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def bg-color 175)
(def font-sz 250)
(def text-ld 10)
(def scaling nil)
(def cur-dim {:w nil :h nil})
(def my-str "TEST\nTEST")
(def nstr (count (re-seq #"[^\s]+" my-str)))

(defn setup-hidden [w h cur-scaling]
  (let* [cur-w (quot w cur-scaling)
         cur-h (quot h cur-scaling)
        ;; want-font (get (q/available-fonts) 0)
         ;;cur-font (q/load-font want-font)
         cur-font (q/load-font "../data/LiberationSans-Bold.ttf")
         new-gr (q/create-graphics cur-w cur-h :p2d)]
    (set! cur-dim (merge cur-dim {:w cur-w :h cur-h}))
    (set! scaling cur-scaling)
    ;(set! text-ld (/ text-ld scaling))
    {:font cur-font :hidden-gfx new-gr}
    )
  )
    
(defn draw-hidden []
  (q/background bg-color)
  (q/text-leading text-ld)
  (q/fill 255)
   (q/text-align :center :center)
  (q/text-size (/ font-sz scaling))
  (q/rect-mode :corner)
  (let [w (get cur-dim :w)
        h (get cur-dim :h)
        ;cur-width (q/text-width my-str)
        ;x1 (- (/ w 2.0) (/ cur-width 2.0))
        ;x2 (+ (/ w 2.0) (/ cur-width 2.0))
        ;actual-height (* (/ font-sz scaling) nstr)
        ;y1 (- (/ h 2.0) (/ actual-height 2.0))
        ;y2 (+ (/ h 2.0) (/ actual-height 2.0))]
        ]
    (q/text my-str (/ w 2) (/ h 2))
    )
  )
         
  
