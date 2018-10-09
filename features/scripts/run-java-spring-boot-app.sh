#!/usr/bin/env bash

sed "s#__BUGSNAG_API_KEY__#$BUGSNAG_API_KEY#g; s#__BUGSNAG_MOCK_PATH__#http://localhost:$MOCK_API_PORT#g;" <"features/fixtures/properties/application.properties" >"features/fixtures/mazerunnerspringboot/src/main/resources/application.properties"

./gradlew bootrun -p features/fixtures/mazerunnerspringboot -Pargs=MOCK_API_PATH=http://localhost:$MOCK_API_PORT,BUGSNAG_API_KEY=$BUGSNAG_API_KEY,EVENT_TYPE=$EVENT_TYPE