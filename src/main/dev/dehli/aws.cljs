(ns dev.dehli.aws
  (:require ["aws-sdk" :as AWS]
            [applied-science.js-interop :as j]
            [cljs-bean.core :refer [->clj ->js]]
            [promesa.core :as p]))

(defn client
  "Constructs a service interface object."
  ([keys] (client keys {}))
  ([keys options]
   (let [Ctr (apply j/get-in [AWS keys])]
     (new Ctr (->js options)))))

(defn invoke
  "Invokes an operation on a client."
  [client {:keys [op request]}]
  (-> client
      (j/call op (->js request))
      (j/call :promise)
      (p/then ->clj)))
