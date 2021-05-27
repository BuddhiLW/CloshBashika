(defn destruct-in-range [coll]
  (vec (range (first coll) (inc (second coll)))))

(defn create-range [coll]
  (vec (map destruct-in-range (partition-list coll))))
