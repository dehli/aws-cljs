(ns aws.core
  (:require ["aws-sdk" :as AWS]
            [applied-science.js-interop :as j]
            [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as cske]
            [cljs-bean.core :refer [->clj ->js]]
            [potpuri.core :as p]
            [promesa.core :as prom]))

(defn service
  "Constructs a service interface object."
  ([keys] (service keys {}))
  ([keys options]
   (let [Ctr (apply j/get-in [AWS (map csk/->PascalCase keys)])]
     (new Ctr (cond-> options

                (map? options)
                (->> (p/map-keys csk/->camelCase)
                     (p/map-vals #(cske/transform-keys csk/->PascalCase %)))

                :always
                ->js)))))

(defn call
  [service method params]
  (-> service
      (j/call (csk/->camelCase method)
              (->js (cske/transform-keys csk/->PascalCase params)))
      (j/call :promise)
      (prom/then #(cske/transform-keys csk/->kebab-case (->clj %)))))
