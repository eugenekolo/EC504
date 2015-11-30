#!/bin/sh
# Builds the backend.

cd ./backend
./gradlew clean
./gradlew shadowJar

