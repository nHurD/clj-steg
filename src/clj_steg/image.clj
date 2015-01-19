(ns clj-steg.image
  (:require [clojure.java.io :as io]
            [clojure.string :as string])
  (:import [javax.imageio ImageIO]
           [java.awt Color]))

(defn read-image [file]
  "Read in an image from the given source"
  (-> file io/as-file ImageIO/read))

 (defn- get-extension [file]
   (last (string/split file #"\.")))

(defn- write-image [src file]
  (ImageIO/write src (get-extension file) file))

(defn- get-rgb [input x y]
  "Gets the rgb component of the given pixel"
  (.getRGB input x y))

(defn get-rgb-values 
  ([input] {:red (bit-and (bit-shift-right input 16) 255)
            :green (bit-and (bit-shift-right input 8) 255)
            :blue (bit-and input 255)})
  ([input x y] (let [rgb-val (get-rgb input x y)]
                (get-rgb-values rgb-val))))

(defn- to-rgb-value [input]
  (.getRGB (new Color (:red input) (:green input) (:blue input))))

(defn set-rgb-value [input x y rgb]
  (let [combined-rgb (to-rgb-value rgb)] (.setRGB input x y combined-rgb)))

