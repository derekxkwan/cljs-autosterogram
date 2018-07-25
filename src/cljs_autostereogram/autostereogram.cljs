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

(defn draw-sgram [input-gfx iw ih]
  (let [in-pix (q/pixels input-gfx)
        far-sep (separation 0.0)]
    (for [j (range ih) ;;go row by row
          :let [same-ptr (atom (vec (range iw))) ;; pointers for equal pixels, points to pixels to right
                pix-val (atom (repeat iw 0)) ;;color of pixels
                ]]
          (for [i (range iw) ;;move through row
            :let [y-col (* j iw) ;;first idx in current row
                  coord (+ i y-col) ;;current coordinate
                  cur-z (/ (q/brightness (get in-pix coord)) 255.0) ;; zo normalized 0-1
                  cur-sep (separation cur-z)
                  cur-left (- i (/ cur-sep 2.0)) ;;what separation of pixels should be given z
                  cur-right (+ cur-left cur-sep)
                  visible? (atom true)]]
                ;;expand out both left and right, check for obscuring surfaces
                ;; by checking z values relative to zt, the z coord of the line of sight for z for both eyes
                ;; the spread is value t 
                (loop [zt 0
                       t 1]
                  (let [z-left (/ (q/brightness (get in-pix (+ (- i t) y-col))) 255.0)
                        z-right (/ (q/brightness (get in-pix (+ (+ i t) y-col))) 255.0)
                        cur-zt (+ cur-z (/ (* 2.0 t (- 2.0 (* mu cur-z))) (* mu e)))
                        visile-now? (and (< z-left zt) (< z-right zt))]
                    (if-not (and (true? visible-now?) (< cur-zt 1))
                      (reset! visible? visible-now?)
                      (recur cur-zt (inc t))
                      )
                    )
                  )
                    
                ;; loop to keep invariant of same-ptr referring to rightward pixels
                (when (true? @visible)
                  (let [temp-left (atom cur-left)
                        temp-right (atom cur-right)]
                    (loop [k (get-in @same-ptr cur-left)]
                      (if (k < @temp-right)
                        (reset! temp-left k)
                        (do (swap! same-ptr assoc @temp-left @temp-right)
                            (reset! temp-left @temp-right)
                            (reset! temp-right k))
                        )
                      (when (and (not= k @temp-left) (not= k @temp-right))
                        (recur @temp-left))
                      )))
                )
          ;; now to actually color pixels
          (loop [x (- iw 1)]
            (if (= (get @same-ptr x) x)
              (swap! pix-val assoc x (int (+ (* (Math/random) 205) 50)))
              (swap! pix-val assoc x (get @pix-val (get @same-ptr x)))
              )
            (q/set-pixel x j (map #(* % (get @pix-val x)) base-color))

            (when (> x 0)
              (recur (dec x)))
            )
          )
    (q/update-pixels)
    ))

                                    
                                    
                    
                      
                  
                  
                  
                
                
    
