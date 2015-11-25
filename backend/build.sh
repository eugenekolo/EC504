#!/bin/sh
# Builds the backend.

gradle clean
gradle build
gradle shadowJar

