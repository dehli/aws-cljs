(ns dev.dehli.aws
  (:require ["aws-sdk" :as aws-sdk]
            [applied-science.js-interop :as j]
            [cljs-bean.core :refer [->clj ->js]]
            [goog.object :as gobj]
            [promesa.core :as p]))

(def ^:private AWS
  "Allows this library to be consumed by cljs, shadow-cljs, and nbb"
  (or (j/get aws-sdk :default) aws-sdk))

(defn client
  "Constructs a service interface object."
  ([keys] (client keys {}))
  ([keys options]
   (let [Ctr (apply j/get-in [AWS keys])]
     (new Ctr (->js options)))))

(defn- ->ex-data [e]
  (let [;; Keys that we don't want showing up
        deny-set #{"clojure$core$protocols$Datafiable$"
                   "clojure$core$protocols$Datafiable$datafy$arity$1"
                   "constructor"
                   "promesa$protocols$IPromiseFactory$"
                   "promesa$protocols$IPromiseFactory$_promise$arity$1"
                   "toString"}]

    (transduce (comp (remove deny-set)
                     (map #(hash-map (keyword %) (gobj/get e %))))
               merge
               (gobj/getAllPropertyNames e))))

(defn invoke
  "Invokes an operation on a client."
  [client {:keys [op request]}]
  (-> client
      (j/call op (->js request))
      (j/call :promise)
      (p/then ->clj)
      (p/catch #(throw (ex-info (j/get % :message)
                                (->ex-data %)
                                %)))))

(defn all-pages
  [client {:keys [key last-key limit op request start-key]}]
  (p/loop [loop-items [] loop-last-key nil]
    (p/let [request*
            (cond-> request
              (some? loop-last-key)
              (assoc start-key loop-last-key)

              (some? limit)
              (assoc :Limit limit))

            response
            (invoke client {:op op :request request*})

            all-items
            (into loop-items (get response key))

            current-last-key
            (get response last-key)

            below-limit?
            (or (nil? limit)
                (< (count all-items) limit))]

      (if (and (some? current-last-key) below-limit?)
        (p/recur all-items current-last-key)
        all-items))))
