(ns clj-steg.encode
  (:require [clj-steg.image :as image]))

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

(defn- message-size-ok?
  "Determine if the message size is smaller than the image"
  [image message]
  (let [width (-> image image/read-image set-image .getWidth)
        height (-> @image-data :image .getHeight)]
    (>= (- (* width height) 2) (count message))))

(defn- apply-image 
  "Apply data to the image"
  [data cnt]
  {:pre [(not (nil? (:image @image-data)))]}
  (->> (-> (image/get-rgb-values (:image @image-data)
                                 (:x @image-data)
                                 (:y @image-data))
           (assoc :red data))
       (image/set-rgb-value (:image @image-data)
                            (:x (swap! image-data assoc :x cnt))
                            (:y @image-data))))

(defn encode-image 
  "Encode the message into the image"
  [image message]
  {:pre [(< 0 (count message)) (message-size-ok? image message)]}
  (when-let [msg (->> (-> (map int message)
                     (conj (count message))
                     (conj (int \E))
                     (interleave (->> (count message) (+ 2) (range))))
                 (partition 2))]
    (doall (map #(apply apply-image %) msg))
    (image/write-image (:image @image-data) "test1.png")))
