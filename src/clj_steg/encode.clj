(ns clj-steg.encode
  (:require [clj-steg.image :as image]))

(defn- apply-pixel [img x y data]
  "Apply data to the given pixel at x and y"
  (let [pixel (image/get-rgb-values img x y)]
    (image/set-rgb-value img x y (assoc pixel :red data))))

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
               current-char (first msg) 
               col (rest msg)]
          (if (nil? current-char)
            img
            (let [new-x (if (> (inc x) width) 0 (inc x))
                  new-y (if (> (inc x) width) (inc y) y)] 
              (apply-pixel img x y current-char)
              (recur new-x new-y (first col) (rest col)))))))))
