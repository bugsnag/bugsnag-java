#!/usr/bin/env bash

if [[ -z "${LOGBACK_FILE}" ]]; then
  rm features/fixtures/mazerunner/src/main/resources/logback.xml
else
  sed "s#__BUGSNAG_API_KEY__#$BUGSNAG_API_KEY#g; s#__BUGSNAG_MOCK_PATH__#http://localhost:$MOCK_API_PORT#g;" <"features/fixtures/logback/$LOGBACK_FILE" >"features/fixtures/mazerunner/src/main/resources/logback.xml"
fi

./gradlew :features:fixtures:mazerunner:bootRun