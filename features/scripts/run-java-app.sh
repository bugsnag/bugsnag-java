#!/usr/bin/env bash

catalina.sh stop

if [[ -z "${LOGBACK_FILE}" ]]; then
  rm features/fixtures/mazerunner/src/main/resources/logback.xml
else
  mkdir features/fixtures/mazerunner/src/main/resources
  cp "features/fixtures/logback/$LOGBACK_FILE" "features/fixtures/mazerunner/src/main/resources/logback.xml"
fi

./gradlew -p features/fixtures/mazerunner bootRun