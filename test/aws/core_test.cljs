(ns aws.core-test
  (:require [applied-science.js-interop :as j]
            [aws.core :as sut]
            [cljs.test :as t]))

(t/deftest service
  (t/testing "DynamoDB DocumentClient"
    (let [client (sut/service [:dynamo-d-b :document-client]
                              {:params {:table-name "foo-bar"}
                               :convert-empty-values true})]

      (t/is (j/get-in client [:options :convertEmptyValues]))
      (t/is (= (j/get-in client [:options :params :TableName]) "foo-bar"))))

  (t/testing "Endpoint"
    (let [endpoint (sut/service [:endpoint] "http://localhost:8000")]
      (t/is (= (j/get-in endpoint [:href]) "http://localhost:8000/")))))
