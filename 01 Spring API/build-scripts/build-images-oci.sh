#!/bin/bash

# Building the app
mvn clean package spring-boot:build-image -DskipTests

# Check if the Maven build was successful
if [ $? -eq 0 ]; then
  echo "Here are your images:"
  docker images | grep 0.0.1-SNAPSHOT
else
  echo "Error: Maven build failed. (Hint: this script must be run from the root of the project, where master pom.xml is.)"
fi