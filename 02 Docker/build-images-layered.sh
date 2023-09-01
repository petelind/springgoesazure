#!/bin/bash

function unpack() {
  FOLDER=$1
  NAME=$2
  VERSION=$3

  CURRENT=$(pwd)

  cd $FOLDER/target
  java -jar -Djarmode=layertools ${NAME}-${VERSION}.jar extract

  cd "$CURRENT"
}

function build() {
  FOLDER=$1
  NAME=$2

  pwd
  docker build -f ./build-scripts/layered/Dockerfile \
    --build-arg JAR_FOLDER=${FOLDER}/target \
    -t ${NAME}:latest \
    -t ${NAME}:layered .
}
APP_VERSION=0.0.1-SNAPSHOT

# Building the app
cd ..

echo "Building JAR files..."
mvn clean package -DskipTests

echo "Unpacking JARs..."
unpack exams-service exams-service ${APP_VERSION}
unpack math-service math-service ${APP_VERSION}
unpack history-service history-service ${APP_VERSION}

echo "Building Docker images..."
build exams-service application/provider-examinator
build math-service application/provider-math
build history-service application/provider-history

echo "Run your app with Docker:
docker --rm -p 8080:8080 --name provider-examinator application/provider-examinator:layered
docker --rm -p 8081:8081 --name provider-math application/provider-math:layered
docker --rm -p 8082:8082 --name provider-history application/provider-history:layered
"