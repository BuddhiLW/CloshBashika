#!/usr/bin/env closh-zero.jar

;; mkdir nome-convertido

;; ls |> (first) |> (map #(str/split % #"teste")) |> (first) |> (second)

(defn photo-id [name]
  (sh (id-ext) |> (first) |> (second) |> (map #'(str/split % #".txt")) |> (first)))

(defn id-string [name]
  (sh (str name)))

(defn ls-first []
  (sh ls |> (first)))

(defn id-extension [filename]
  (->
   (str/split filename #"teste")
   (first)
   (second)))

(defn split-teste [filename]
  (->
   (str/split filename #"teste")
   (second)))
