(ns chp11-parallelism.funds
  (:require (clojure [xml :as xml]))
  (:require (clojure [zip :as zip])))

;;convenience function first seen in at nakkaya.com later in clj.zip src
(defn zip-str
  [s]
  (zip/xml-zip
    (xml/parse  (java.io.ByteArrayInputStream. (.getBytes s)))))
  
(defn zip-str%
  [s]
  (->> (xml/parse (java.io.ByteArrayInputStream. (.getBytes s)))
       (zip/xml-zip)))
     

;; parse from xml strings to internal xml representation.

;; (zip-str "<a href='nakkaya.com'/>")

;;root can be rendered with xml/emit-element
;; (xml/emit-element (zip/root [{:tag :a, :attrs {:href "nakkaya.com"}, :content nil} nil]))

;; A review on emit element
(comment
         ;; if just a string it's text inside or something. just print it.
         (xml/emit-element "hello")
         ;=>hello
         (xml/emit-element {:tag "hello"})
         ;=> <hello/>
         (xml/emit-element {:tag :hello :attrs {:place "world"}}))
         
;;content is for all of the children in the element
(comment (xml/emit-element {:tag :parent :attrs {:id "22" :name "fritz"} :content [
                {:tag :child :attrs {:id "56"}}
                {:tag :child :attrs {:id "57"}}]}))
              
(xml/emit-element
                  {:tag :lisp :attrs {:inventor "John McCarthy" :domain "Artificial Intelligence"}
                   :content [
                             {:tag :clojure :attrs {:age 10}}
                             {:tag :scheme :attrs {:age 30}}
                             {:tag :racket :attrs {:age 20}}
                             {:tag "Common Lisp" :attrs {:age 30}}]})
                                         
                                            
