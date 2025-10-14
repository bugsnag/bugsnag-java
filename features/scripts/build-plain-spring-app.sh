#!/usr/bin/env bash

# Start Tomcat then copy a WAR file across to serve it
catalina.sh start
./gradlew -p features/fixtures/mazerunnerplainspring6 war
cp features/fixtures/mazerunnerplainspring6/build/libs/mazerunnerplainspring.war $CATALINA_HOME/webapps/ROOT.war
