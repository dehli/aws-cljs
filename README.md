# aws-cljs

**Warning: This library is in alpha!**

## Usage

```clj
(ns my-ns.core
  (:require [dev.dehli.aws :as aws]))

(def db-client
  (aws/client [:DynamoDB :DocumentClient]
              {:params {:TableName "foo"}))

(defn put-client
  [client item]
  (aws/invoke {:op :put :request {:Item item}}))
```

## Scripts

```bash
clj -M:outdated # Check for outdated dependencies
clj -M:test     # Run tests

clj-kondo --lint ./src/
```
