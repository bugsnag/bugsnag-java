#!/usr/bin/env bash

# Start Tomcat then copy a WAR file across to serve it
catalina.sh stop
catalina.sh start
./gradlew -p features/fixtures/mazerunnerplainspring war
cp features/fixtures/mazerunnerplainspring/build/libs/mazerunnerplainspring.war $CATALINA_HOME/webapps/ROOT.war
