(ns devibrary.partitioning
  (:require [clojure.java.shell :refer [sh]]
            [clojure.string :as str]))

(def p-list [1 3 7 11 14 17 20 23 26 31 35 39 42 45 448 51 54 57 60 63 66 70 74 77 84 87 90 93 96 99 102 105 108 11 115 118 121 127 130 133 137 140 144 147 150 154 157 160 163 166 169 172 175 179 182 186 189 192 196 201 205 211 214 218 222 225 228 232 235 238 241 245 248 251 254 257 260 263 266 269 272 276 279 283 287 290 294 298 301 304 307 310 313 316 320 324 327 330 334 337 340 343 347 350 354 357 360 363 367 370 373 377 380 384 387 390 393 396 399 402 405 409 412 415 418 421 425 428 431 434 438 441 444 447 450 455 459 462 465 469 473 476 480 484 487 492 495 498 501 504 507 510 514 519 522 525 528 535 538 541 544 547 550 553 556 559 562 565 568 571 575 578 581 584 587 590 593 596 599 605 611 614 617 620 624 627 630 633 636 639 642 645 648 651 654 658 663 666])

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

(defn populate-key-map
  "Given a interval-collection; colletion of file-ids;
   and which folder to start; this function will create
   a directory structure correspondent to it.
   =coll-file-id= is to come from closh-pipe;
   =coll-map= is the refined partition-scheme data;
   =start= is which folder to start"
  [coll-map coll-file-id start]
  (loop [dir-map []
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

(defn which-ids-subset-contains?
  "Returns list of pertained ids of the =id-list=
  to the first subset of the =set=."
  [id-list set]
  (filter #(not (nil? %))
          (map (fn [e] (some (hash-set e) (first set))) id-list)))

(defcmd create-populate-dir
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
        (println (str p-vec))
        (sh mkdir (identity (str (re-pattern reg-dir)
                                 (first (first p-vec)))))
        (loop [file-ids (second (first p-vec))
               reg-dir reg-dir
               reg1-file reg1-file
               reg2-file reg2-file]
          (if (empty? file-ids)
            (println "One more directory done.")
            (do (sh mv
                    (identity (str (re-pattern reg1-file)
                                   (first file-ids)
                                   (re-pattern reg2-file)))
                    (identity (str (re-pattern reg-dir)
                                   (first (first p-vec)))))
                (recur (drop 1 file-ids)
                       (identity reg-dir)
                       (identity reg1-file)
                       (identity reg2-file)))))
        (recur (drop 1 p-vec)
               (identity reg-dir)
               (identity reg1-file)
               (identity reg2-file))))))

(def raw-data [0 3 7 10])
