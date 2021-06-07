(ns devibrary.rename)

(defn split-reg [filename reg] 
  (str/split filename (re-pattern reg)))

(defn file-ids [file reg1 reg2]
  (->
   (split-reg file reg1)
   (second)
   ((fn [s] (split-reg s reg2)))
   (first)
   ((fn [s] (Integer/parseInt s)))))

(defn clj-rn [file reg1 arg reg2]
  (sh mv (identity file) (str/join [reg1 arg reg2])))

(defcmd rename-file [reg11 reg12 c reg21 reg22]
  (sh ls |> (map #(file-ids % reg11 reg12)) |> (map #(- % c)) |> (map str) |> (map #(clj-rn %1 reg21 %2 reg22) (sh ls |> (identity)))))

;; (sh rename-test "test" ".txt" 5 "does-it-work" ".org")

(defn dir-ids [file reg1 reg2]
  (->
   (split-reg file reg1)
   (second)
   ((fn [s] (Integer/parseInt s)))))

(defn clj-rn-dir [dir reg1 arg]
  (sh mv (identity dir) (str/join [reg1 arg])))

(defcmd rename-dir [reg11 c reg21]
  (sh ls -d */ |> (map #(dir-ids % reg11)) |> (map #(- % c)) |> (map str) |> (map #(clj-rn-dir %1 %2) (sh ls -d */ |> (identity)))))
(defcmd rename-test [reg11 reg12 c reg21 reg22]
(sh ls |> (map #(file-ids % reg11 reg12)) |> (map #(- % c)) |> (map str) |> (map #(clj-rn %1 reg21 %2 reg22) (sh ls |> (identity)))))
