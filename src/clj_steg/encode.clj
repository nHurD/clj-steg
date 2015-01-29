(ns clj-steg.encode
  (:require [clj-steg.image :as image]))

(def ^:private image-data (atom {:image nil :x 0 :y 0}))

(add-watch image-data :on-change
           (fn [k atm old-state new-state]
             {:pre [(not (nil? (:image new-state)))]}
             (when (>= (:x new-state) (-> new-state :image .getWidth dec))
               (swap! atm
                      assoc
                      :x 0
                      :y (-> new-state :y inc)))))

(defn- set-image
  "Sets the image into the atom for later use"
  [image]
  (:image (swap! image-data assoc :image image)))

(defn- get-length-data
  "Determines the length information based on the given message"
  [message]
  (loop [shift-amount 0 value (-> (count message) (bit-and 255)) res []]
    (if (= 0 value)
      (cons (count res) res)
      (recur (inc shift-amount)
             (-> (count message)
                 (bit-shift-right (bit-shift-left 8 shift-amount))
                 (bit-and 255))
             (conj res value)))))

(defn- message-size-ok?
  "Determine if the message size is smaller than the image"
  [image message]
  (let [width (-> image image/read-image set-image .getWidth)
        height (-> @image-data :image .getHeight)]
    (>= (- (* width height)
           (-> (get-length-data message) (count) (+ 1))))))

(defn- test-apply
  [data]
  {:pre [(not (nil? (:image @image-data)))]}
  (let [x (:x (swap! image-data
                     assoc :x (-> @image-data :x inc)))
        y (:y @image-data)]
    (printf "x %s y %s, data: %s\n" x y data)))

(defn- test-set-value
  [img x y data]
  (printf "x: %s, y: %s, data: %s" x y data))

(defn- apply-image
  "Apply data to the image"
  [data]
  {:pre [(not (nil? (:image @image-data)))]}
  (->> (-> (image/get-rgb-values (:image @image-data)
                                 (:x (swap! image-data
                                        assoc :x (-> @image-data :x inc)))
                                 (:y @image-data))
           (assoc :red data))
       (image/set-rgb-value (:image @image-data)
                            (:x @image-data)
                            (:y @image-data))))

(defn encode-image
  "Encode the message into the image"
  [image message]
  {:pre [(< 0 (count message)) (message-size-ok? image message)]}
  (when-let [msg (-> (map int message)
                     (conj (get-length-data message))
                     (conj (int \E))
                     (flatten))]
    (doall (map apply-image msg))))
