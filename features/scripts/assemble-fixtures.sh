#!/usr/bin/env bash

# Remove existing directory if it exists
if [ -d "features/fixtures/libs" ]; then
    rm -rf features/fixtures/libs
fi

# Recreate the directory
mkdir -p features/fixtures/libs

# Unzip the contents into it
unzip maven-repository.zip -d features/fixtures/libs