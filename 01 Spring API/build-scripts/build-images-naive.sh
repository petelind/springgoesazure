#!/bin/bash

function build_basic() {
  JAR_FILE=$1
  APP_NAME=$2

  docker build -f ./build-scripts/basic/Dockerfile \
    --build-arg JAR_FILE=${JAR_FILE} \
    -t ${APP_NAME}:latest \
    -t ${APP_NAME}:naive .
}

APP_VERSION=0.0.1-SNAPSHOT

# Building the app
cd ..

echo "Building JAR files"
mvn clean package -DskipTests

echo "Building Docker images"
build_basic ./exams-service/target/exams-service-${APP_VERSION}.jar application/provider-examinator
build_basic ./math-service/target/math-service-${APP_VERSION}.jar application/provider-math
build_basic ./history-service/target/history-service-${APP_VERSION}.jar application/provider-history