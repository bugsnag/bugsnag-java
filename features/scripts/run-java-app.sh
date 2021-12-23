#!/usr/bin/env bash

catalina.sh stop

if [[ -z "${LOGBACK_FILE}" ]]; then
  rm features/fixtures/mazerunner/src/main/resources/logback.xml
else
  mkdir features/fixtures/mazerunner/src/main/resources
  sed "s#__BUGSNAG_API_KEY__#$BUGSNAG_API_KEY#g; s#__BUGSNAG_MOCK_PATH__#http://localhost:$MOCK_API_PORT#g;" \
   < "features/fixtures/logback/$LOGBACK_FILE" \
   > "features/fixtures/mazerunner/src/main/resources/logback.xml"
fi

./gradlew -p features/fixtures/mazerunner bootRun