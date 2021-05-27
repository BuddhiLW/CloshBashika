#!/usr/bin/env bb

(ns devibrary.partitioning
  (:require [clojure.java.shell :refer [sh]]
            [clojure.string :as str]))

(defn generate-pairs [coll]
  (loop [new-coll []
         db coll]
    (if (or (= (count db) 0) (= (count db) 1))
      new-coll
      (recur
       (conj new-coll (vec (take 2 db)))
       (drop 2 db)))))

(defn complete-list [coll]
  (loop [new-coll (vec (take 2 coll))
         remaining-coll (vec (drop 2 coll))]
    (if (= (count remaining-coll) 0)
      new-coll
      (recur
       (into new-coll
             (vec (list (inc (last new-coll))
                        (first remaining-coll))))
       (drop 1 remaining-coll)))))

(defn partition-list [coll]
  (->
   (complete-list coll)
   (generate-pairs)))

(defn destruct-in-range [coll]
  (vec (range (first coll) (inc (second coll)))))

(defn create-range [coll]
  (vec (map destruct-in-range (partition-list coll))))

(defn which-ids-subset-contains?
  "Returns list of pertained ids of the =id-list=
  to the first subset of the =set=."
  [id-list set]
  (filter #(not (nil? %))
          (map (fn [e] (some (hash-set e) (first set))) id-list)))

(defn populate-key-map
  "Given a interval-collection; colletion of file-ids;
   and which folder to start; this function will create
   a data-structure ready to =create-populate-dir= use.
   =coll-file-id= is to come from closh-pipe;
   =coll-map= is the refined partition-scheme data;
   =start= is which folder to start"
  [coll-map coll-file-id start]
  (loop [dir-map {}
         nth-dir start
         subset coll-map
         id-list coll-file-id]
    (if (empty? subset)
      dir-map
      (recur
       (conj dir-map (vec (list
                           nth-dir
                           (which-ids-subset-contains? id-list subset))))
       (inc nth-dir)
       (drop 1 subset)
       (identity id-list)))))


(defn create-populate-dir
  "Takes =p-vec= (persistent vector) with dir-number and img-number data;
takes the =reg-dir=, directory-name regular-expression;
takes the =reg1-file=, file-name-base regular-expression;
takes the =reg2-file=, file-name-extesion regular-expression
Returns a folder-structure populated with the given p-vec relation"
  [p-vec reg-dir reg1-file reg2-file]
  (loop [p-vec p-vec
         reg-dir reg-dir
         reg1-file reg1-file
         reg2-file reg2-file]
    (if (empty? p-vec)
      (println "All done. Check your directories.")
      (do
        (sh "mkdir" (str (re-pattern reg-dir) (first p-vec)))
        (loop [file-ids (second (first p-vec))
               reg1-file reg1-file
               reg2-file reg2-file]
          (if (empty? file-ids)
            (println "One more directory done.")
            (do (sh "mv"
                    (identity (str (re-pattern reg1-file)
                                   (first file-ids)
                                   (re-pattern reg2-file)))
                    (identity (str (re-pattern reg-dir)
                                   (first p-vec))))
                (recur (drop 1 file-ids) (identity reg1-file) (reg2-file)))))
        (recur (drop 1 p-vec) (identity reg-dir) (identity reg1-file) (identity reg2-file))))))
