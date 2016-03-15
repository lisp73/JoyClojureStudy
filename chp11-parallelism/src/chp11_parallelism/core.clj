(ns chp11-parallelism.core
  (:require (clojure [xml :as xml]))
  (:require (clojure [zip :as zip]))
  (:import  (java.util.regex Pattern)))

       
;;LISTING 11.1 CONVERTING AN XML FEED TO AN XML ZIPPER
(defn feed->zipper [uri-str]
  (->> (xml/parse uri-str) ;; parse xml
       zip/xml-zip)) ;; convert to zipper
     
;; LISTING 11.2 NORMALIZING RSS and ATOM feed entries to a common structure
(defn normalize [feed]
  (if (= :feed (:tag (first feed))) ;; a quick and dirty detect
      feed
      (zip/down feed)))

(defn feed-children
  [uri-str]
  (->> uri-str
       feed->zipper ;;zipperfy
       normalize ;;normalize
       zip/children
       (filter (comp #{:item :entry} :tag)))) ;;Grab entries

;;LISTING 11.3 Retreiving the title text from the normalized feed structure
(defn title
  [entry]
  (some->> entry
           :content
           (some #(when (= :title (:tag %)) %))
           :content
           first))

;;LISTING 11.4 FUNCTION TO COUNT THE NUMBER OF OCCURENCES OF TEXT.
(defn count-text-task
  [extractor txt feed] ;; we take a function to take the text
  (let [items (feed-children feed)
        re    (Pattern/compile (str "(?i)" txt))]
      (->> items
           (map extractor) ;;Get children text
           (mapcat #(re-seq re %)) ;;match against each
           count)))
         
;; Here we use the COUNT-TEXT-TASK to find some text in the titles of the RSS feed for the ELixir language blog.

(comment
         (count-text-task
           title
           "Erlang"
           "http://feeds/feedburner.com/ELixirLang")
         )
         ;; end of comment
       
(comment
         (count-text-task
           title
           "ELixir"
           "http://feeds.feedburner.com/ELixirLang")
);; end of comment ...

;; But wait, there is a better way to sdo this.
;;COmputing the results of count-text-task is performed over a sequwnce of RSS or Atom feeds; we can spread them over a number of threads for parallel processing using the follwoing code

;;listing 11.5 MANUALLY SPREADING TASKS OVER A SEQUENCE OF FUTURES.
(def feeds #{"http://feeds.feedburner.com/ELixirLang" ;;feeds
             "http://blog.fogus.me/feed/"})

;;we could parallelism here
(comment
         (let [results (for [feed feeds]
                   (future
                     (count-text-task title "Lisp" feed)))]
                   (reduce + (map deref results)))
);;end of comment.

;;We can also create a convenienence MAcro to dispatch a sequence of futures
(comment
        ; Here we simplfy the macro with the {:as} and the => symbol to clearly
        delineate the its segments. The as-futures body exits only after the task body finished - as determined by the executions of the futures. You can use as-futures to perform count-text-task with a new function occurrences, implemented here)


        
        
        
(defmacro as-futures [[a args] & body]
  (let [parts (partition-by #{'=>}body) ;;Parallel actions are separated from the summation by =>
        [acts _ [res]] (partition-by #{:as} (first parts)) ;;name the results
        [_ _ task] parts]
      `(let [~res (for [~a ~args](future ~@acts))] ;;wrap wach action in a future
            ~@task)))
          
(defn occurrences
  [extractor tag & feeds]
  (as-futures [feed feeds]
              (count-text-task extractor tag feed)
              :as results
              =>
              (reduce + (map deref results))))
          
;;finally we can use the above code like this..
;(occurrences title "released"
;  "http://blog.fogus.me/feed/"
;  "http://feeds.feedburner.com/ElixirLang"
;  "http://www.ruby-lang.org/en/feeds/news.rss")
          
;;we can use it like this
;;(as-futures [<arg-name> <all-args>]
;;  <actions-using-args>
;;  :as <results-name>
;; =>
;;  <actions-using-results>)


;;


        
           




         

     

     


