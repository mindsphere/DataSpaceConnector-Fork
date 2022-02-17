
# Transfer HTTP to HTTP

This demo configuration uses specific implementations of `DataReader` and `DataWriter` interfaces to do a file transfer using
pure HTTP.

The demo can be run with a single connector acting as both consumer and provider. Destination URL must be provided with artifact request.
Source URL can be specified via `edc.demo.http.url` property.

## Steps

* Get offers
* Negotiate contract, wait for the agreement
* Request the artifact

Please use [postman collection](samples/other/file-transfer-http-to-http/postman/IDS Eclipse HTTP to HTTP.postman_collection.json).

## Build

    ./gradlew :samples:other:file-transfer-http-to-http:shadowJar

## Run

    java -Dedc.fs.config=samples/other/file-transfer-http-to-http/config.properties -jar samples/other/file-transfer-http-to-http/build/libs/file-transfer-http-to-http.jar