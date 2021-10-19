(ns dev.dehli.aws-test
  (:require [cljs.test :as t]
            [dev.dehli.aws :as sut]
            [promesa.core :as p]))

(t/deftest invoke-exception
  (t/async done
    (p/let [error (-> (sut/client [:DynamoDB :DocumentClient])
                      (sut/invoke {:op :put :request {:Item {:pk "foo"}}})
                      (p/then (constantly nil))
                      (p/catch identity))]

      (t/is (instance? ExceptionInfo error))
      (t/is (= (:code (ex-data error))
               (:name (ex-data error))
               "MissingRequiredParameter"))

      (t/is (some? (:stack (ex-data error))))
      (t/is (some? (:time (ex-data error))))

      (done))))
