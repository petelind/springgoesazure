#!/bin/bash

if [ $# -eq 0 ]; then
    echo "Error: Missing arguments. Please provide acrname (just the name, not the url) and acrpassword."
    exit 1  # Exit the script with a non-zero status code
fi

docker login $1.azurecr.io --username $1 --password $2
APP_VERSION=0.0.1-SNAPSHOT

function build_basic() {
  JAR_FILE=$1
  APP_NAME=$2
  BASE_URL=$3.azurecr.io
  POINTER=$(git rev-parse --short HEAD)

  docker build -f ./build-scripts/docker/basic/Dockerfile \
    --build-arg JAR_FILE=${JAR_FILE} \
     -t $BASE_URL/${APP_NAME}:latest
     -t $BASE_URL/${APP_NAME}:${POINTER} .

  docker push --all-tags $BASE_URL/${APP_NAME}:latest
}


# Building the app
cd ..

echo "Building JAR files..."
mvn clean package -DskipTests -P with-kubernetes

echo "Building Docker images..."
build_basic ./math-service/target/math-service-${APP_VERSION}.jar provider-math
build_basic ./history-service/target/history-service-${APP_VERSION}.jar provider-history
build_basic ./exams-service/target/exams-service-${APP_VERSION}.jar provider-examinator

echo "Here are your images:"
docker images | grep kubernetes