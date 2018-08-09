(ns cljs-autostereogram.sgram
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            ))

(def cur-dim {:w nil :h nil})
(def dpi 191.0)
(def mu (/ 1 3.0))
(def scaling 4.0)
(def conv_rad 20) ;;convergence circle radius
(def conv_ypos (/ 3.0 4.0))
(def base-color nil) ;; normalized color
(def sum-thresh 2.25) ;;threshold components of base-color should sum up to
(def e nil)

(defn calculate-e []
  (Math/round (* 2.5 dpi))
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

(defn setup-sgram [w h cur-scaling]
  (let [real-w (quot w cur-scaling)
        real-h (quot h cur-scaling)]
    (set! cur-dim (merge cur-dim {:w real-w :h real-h}))
    (set! base-color (randomize-color))
    (set! dpi (/ dpi cur-scaling))
    (set! e (calculate-e))
    (q/create-graphics real-w real-h :p2d)
    )
  )

;;get normalized brightness
(defn get-brightness-norm [pixl]
  (/ (q/brightness pixl) 255.0))

(defn draw-sgram [input-gfx iw ih]

  (let [in-pix (q/pixels input-gfx)
        far-sep (separation 0.0)
        same-ptr (atom (vec (range iw)))
        pix-val (atom (vec (repeat iw 0)))]
    (doseq [j (range ih) ;;go row by row
            i (range iw)]
            (let [y-col (* j iw) ;;first idx in current row
                  coord (+ i y-col) ;;current coordinate
                  cur-z (get-brightness-norm (get in-pix coord)) ;; zo normalized 0-1
                  cur-sep (separation cur-z)
                  cur-left (atom (- i (/ cur-sep 2.0))) ;;what separation of pixels should be given z
                  cur-right (atom (+ @cur-left cur-sep))
                  visible? (atom true)]
            ;;expand out both left and right, check for obscuring surfaces
            ;; by checking z values relative to zt, the z coord of the line of sight for z for both eyes
            ;; the spread is value t 
              (loop [zt 0
                     t 1]
                (let [z-left (get-brightness-norm (get in-pix (+ (- i t) y-col)))
                      z-right (get-brightness-norm (get in-pix (+ (+ i t) y-col)))
                      cur-zt (+ cur-z (/ (* 2.0 t (- 2.0 (* mu cur-z))) (* mu e)))
                      visible-now? (and (< z-left zt) (< z-right zt))]
                  (if-not (and (true? visible-now?) (< cur-zt 1))
                    (reset! visible? visible-now?)
                    (recur cur-zt (inc t))
                    )
                  )
                )
              
            ;; loop to keep invariant of same-ptr referring to rightward pixels
              (when (true? @visible?)
                  (loop [k (get @same-ptr @cur-left)]
                    (if (k < @cur-right)
                      (reset! cur-left k)
                      (do (swap! same-ptr assoc @cur-left @cur-right)
                          (reset! cur-left @cur-right)
                          (reset! cur-right k))
                      )
                    (when (and (not= k @cur-left) (not= k @cur-right))
                      (recur @cur-left))
                    )
                  (swap! same-ptr assoc @cur-left @cur-right)
                  )
          ;; now to actually color pixels
              (when (= i (- iw 1))

                (loop [x (- iw 1)]
                  (if (= (get @same-ptr x) x)
                    (swap! pix-val assoc x (int (+ (* (Math/random) 205) 50)))
                    (swap! pix-val assoc x (get @pix-val (get @same-ptr x)))
                    )
                  ;;(println (map #(* % (get @pix-val x)) base-color))
                  (q/set-pixel x j (apply q/color (map #(* % (get @pix-val x)) base-color)))

                  (when (> x 0)
                    (recur (dec x)))
                  )
                ;; reset init conditions
                (reset! same-ptr (vec (range iw)))
                (reset! pix-val (vec (repeat iw 0)))
                )
              )))
  (q/update-pixels)
  )

                                    
                                    
                    
                      
                  
                  
                  
                
                
    
