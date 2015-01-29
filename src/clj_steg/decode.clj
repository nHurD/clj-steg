(ns clj-steg.decode
  (require [clj-steg.image :as image]))

(def ^:private image-data (atom {:image nil :x 0 :y 0}))

(add-watch image-data :on-change 
           (fn [k atm old-state new-state]
             {:pre [(not (nil? (:image new-state)))]}
             (when (>= (:x new-state) (-> new-state :image .getWidth dec))
               (swap! atm assoc :x 0 :y (-> new-state :y inc)))))

(defn- set-image 
  "Sets the image into the atom for later use"
  [image] 
  (:image (swap! image-data assoc :image image)))

(defn- has-encoding? [img]
  "Ensure our image does indeed have encoding data set"
  (= (char (:red (image/get-rgb-values img 0 0))) \E))


;; (defn- process-image [img]
;;   "Decode the image"
;;   (let [msg-length (get-message-length img)
;;         image-width (.getWidth img)
;;         image-height (.getHeight img)]
;;     (loop [x 2 y 0 message ()  cnt 0]
;;       (if (>= cnt  msg-length)
;;         (apply str (reverse message))
;;         (recur 
;;          (if (>= (inc x) image-width) 0 (inc x))
;;          (if (>= (inc x) image-width) (inc y) y)
;;          (cons (char (:red (image/get-rgb-values img x y))) message) 
;;          (inc cnt))))))
;; 
;; 
;; (defn decode-image [file]
;;   "Decode data from the given file"
;;   (let [img (image/read-image file)]
;;     (if-not (has-encoding? img) "" (process-image img))))
