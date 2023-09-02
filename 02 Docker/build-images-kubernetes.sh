#!/bin/bash

function build_basic() {
  JAR_FILE=$1
  APP_NAME=$2

  docker build -f ./build-scripts/basic/Dockerfile \
    --build-arg JAR_FILE=${JAR_FILE} \
    -t ${APP_NAME}:kubernetes .
}

APP_VERSION=0.0.1-SNAPSHOT

# Building the app
cd ..

echo "Building JAR files for Kubernetes..."
mvn clean package -DskipTests -P with-kubernetes

echo "Building Docker images for Kubernetes..."

build_basic ./math-service/target/math-service-${APP_VERSION}.jar provider-math
build_basic ./history-service/target/history-service-${APP_VERSION}.jar provider-history
build_basic ./exams-service/target/exams-service-${APP_VERSION}.jar provider-examinator

echo "Here are your images:"
docker images | grep kubernetes