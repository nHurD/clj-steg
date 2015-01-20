(ns clj-steg.decode
  (require [clj-steg.image :as image]))

(defn- has-encoding? [img]
  "Ensure our image does indeed have encoding data set"
  (= (char (:red (image/get-rgb-values img 0 0))) \E))

(defn- get-message-length [img]
  "Get the message length"
  (:red (image/get-rgb-values img 1 0)))

(defn- process-image [img]
  "Decode the image"
  (let [msg-length (get-message-length img)
        image-width (.getWidth img)
        image-height (.getHeight img)]
    (loop [x 2 y 0 message ()  cnt 0]
      (if (>= cnt  msg-length)
        (apply str (reverse message))
        (recur 
         (if (> (inc x) image-width) 0 (inc x))
         (if (> (inc x) image-width) (inc y) y)
         (cons (char (:red (image/get-rgb-values img x y))) message) 
         (inc cnt))))))


(defn decode-image [file]
  "Decode data from the given file"
  (let [img (image/read-image file)]
    (if-not (has-encoding? img) "" (process-image img))))
