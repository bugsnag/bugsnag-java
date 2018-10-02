#!/usr/bin/env bash

./gradlew bootrun -p features/fixtures/mazerunner -Pargs=MOCK_API_PATH=http://localhost:$MOCK_API_PORT,BUGSNAG_API_KEY=$BUGSNAG_API_KEY,EVENT_TYPE=$EVENT_TYPE