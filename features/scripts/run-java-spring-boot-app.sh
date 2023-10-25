#!/usr/bin/env bash

catalina.sh stop
if [[ "${JAVA_VERSION}" == "8"* ]]; then
    cd features/fixtures/mazerunnerspringboot
    ./gradlew bootRun
else
    ./gradlew -p features/fixtures/mazerunnerspringboot3 bootRun
fi