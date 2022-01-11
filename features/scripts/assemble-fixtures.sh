#!/usr/bin/env bash

mkdir -p features/fixtures/libs
./gradlew bugsnag:assemble bugsnag-spring:assemble -Pversion=9.9.9-test
cp bugsnag/build/libs/bugsnag-9.9.9-test.jar features/fixtures/libs/bugsnag-9.9.9-test.jar
cp bugsnag-spring/build/libs/bugsnag-spring-9.9.9-test.jar features/fixtures/libs/bugsnag-spring-9.9.9-test.jar
