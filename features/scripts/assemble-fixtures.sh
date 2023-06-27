#!/usr/bin/env bash

if [ ! -d "features/fixtures/libs" ]; then
    mkdir -p features/fixtures/libs
    unzip maven-repository.zip -d features/fixtures/libs
fi
