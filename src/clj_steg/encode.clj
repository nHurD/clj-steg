(ns clj-steg.encode
  (:require [clj-steg.image :as image]))

(defn encode-image [image message]
  "Encode the message into the image"
  (let [img (image/read-image image)
        width (.getWidth img)
        height (.getHeight img)]
    (if (> (count message) (* width height))
      (throw (Exception. "Message is too large for this image"))
      (let [msg (-> (map int message)
                    (conj (count message))
                    (conj (int \E)))]
        (loop [x 0 
               y 0 
               pixel (image/get-rgb-values img x y) 
               current-char (first msg) 
               col (rest msg)]
          (if (nil? current-char)
            img
            (let [new-x (if (> (inc x) width) 0 (inc x))
                  new-y (if (> (inc x) width) (inc y) y)] 
              (image/set-rgb-value img x y (assoc pixel :red current-char))
              (recur 
               new-x 
               new-y 
               (image/get-rgb-values img new-x new-y) 
               (first col) 
               (rest col)))))))))
