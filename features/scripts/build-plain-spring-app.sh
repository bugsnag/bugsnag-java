#!/usr/bin/env bash

# Start Tomcat then copy a WAR file across to serve it
catalina.sh start
if [[ "${JAVA_VERSION}" == "8"* ]]; then
    ./gradlew -p features/fixtures/mazerunnerplainspring war
    cp features/fixtures/mazerunnerplainspring/build/libs/mazerunnerplainspring.war $CATALINA_HOME/webapps/ROOT.war
else
    ./gradlew -p features/fixtures/mazerunnerplainspring6 war
    cp features/fixtures/mazerunnerplainspring6/build/libs/mazerunnerplainspring.war $CATALINA_HOME/webapps/ROOT.war
fi