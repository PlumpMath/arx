(ns arx.util)


;; Stuff for evaluating things inside of `draw` or other Quil
;; functions (i.e. when graphics context is required):
(def pseudo-repl-result (atom nil))


(defmacro pseudo-repl [& body]
  `(try
     (reset! pseudo-repl-result (do ~@body))
     (catch Throwable t#
       (reset! pseudo-repl-result t#))))


(defn pr-result [] @pseudo-repl-result)
