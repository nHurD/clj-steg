(ns clj-steg.decode
  (require [clj-steg.image :as image]))

(def ^:private image-data (atom {:image nil :x -1 :y 0}))

(add-watch image-data :on-change
           (fn [k atm old-state new-state]
             {:pre [(not (nil? (:image new-state)))]}
             (when (>= (:x new-state) (-> new-state :image .getWidth dec))
               (swap! atm assoc :x 0 :y (-> new-state :y inc)))))

(defn set-image
  "Sets the image into the atom for later use"
  [image]
  (:image (swap! image-data assoc :image image)))

(defn- has-encoding?
  "Ensure our image does indeed have encoding data set"
  []
  {:pre [(not (nil? (:image @image-data)))]}
  (= \E (-> @image-data :image (image/get-rgb-values 0 0) :red (char))))

(defn- get-pixel-data
  "Gathers pixel data from our image"
  [& {:keys [n] :or {n 1}}]
  {:pre [(not (nil? (:image @image-data)))]}
  (repeatedly n (fn [] (image/get-rgb-values
                 (:image @image-data)
                 (:x (swap! image-data assoc :x (-> @image-data :x inc)))
                 (:y @image-data)))))


(defn- get-encoding-length-data
  "Gather length data from the image"
  []
  {:pre [(not (nil? (:image @image-data)))]}
  (swap! image-data assoc :x 1)
  (let [data-length (-> @image-data :image (image/get-rgb-values 1 0) :red)
        lengths (->> (-> (map :red (get-pixel-data :n data-length))
                         (interleave (range data-length)))
                     (partition 2))]
    (reduce (fn [res data]
              (let [[num shift-amt] data]
                (-> (bit-shift-left num (* 8 shift-amt))
                    (bit-or res))))
            0
            lengths)))

(defn process-image
  "Process the image and return the results"
  []
  {:pre [(not (nil? (:image @image-data))) (has-encoding?)]}
  (->> (get-encoding-length-data)
       (get-pixel-data :n)
       (map :red)
       (map char)
       (apply str)))

(defn decode-image
  [image]
  (reset! image-data {:image nil :x -1 :y 0})
  (-> image (image/read-image) (set-image))
  (process-image))
