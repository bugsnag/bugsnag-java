#!/usr/bin/env bash

catalina.sh stop
./gradlew -p features/fixtures/mazerunnerspringboot3 bootRun