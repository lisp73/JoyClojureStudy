(ns chp11-parallelism.play)
(ns joy.futures
  (:require (clojure [xml :as xml]))
  (:require (clojure [zip :as zip]))
  (:import (java.util.regex Pattern)))

;; this was the example known as atomic syndicatin format.
;; Here we create a simple node structure with tables with keys. ..

;;Converting XML feeds to XML zipper.

;; Here I parse the XML and put it into a convenient format to make sense of the feed.
(defn feed->zipper
  [uri-str]
  (-> (xml/parse uri-str)
      zip/xml-zip))

;;SO What does, clojure.xml/parse do?
;; You can retrieve the XML for a twitter RSS feed and convert it into the familiar tree format.
;; What happens when we the familiar tree from parse is transferred to xml-zip
;;ans - Converts the datastructure into another strucutre called a zipper. Yhis allows you to esuly navigate down from the root rss XML Node , where you then reteive its children.


;; Here we take the zipper from the XML parse step and detect the precise feed type(RSS or Atom ), you can normalize the feed elements into a common structure.

;; 11.2
(defn normalize-feed
  [feed]
  (if (= :feed (:tag (first feed)))
    :feed
    (zip/down feed))) ;; normaluze to a similar xml tree
  
  ;; Retriving the
  



