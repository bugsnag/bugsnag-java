#!/usr/bin/env bash
./gradlew -xsignPublicationPublication -Preleasing=true -Pversion=9.9.9-test publishPublicationPublicationToTestRepository
cd build/repository/ || exit
zip -r ../../maven-repository.zip ./*
