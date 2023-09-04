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
  BASE_URL=$3.azurecr.io # Username and repo name are the same, so we can use the same variable
  POINTER=$(git rev-parse --short HEAD)

  echo "Target registry is: $BASE_URL"
  echo "Tagging images as $BASE_URL/${APP_NAME}:latest and $BASE_URL/${APP_NAME}:${POINTER}... "
#  docker build -f ./build-scripts/basic/Dockerfile \
#    --build-arg JAR_FILE=${JAR_FILE} \
#     -t $BASE_URL/${APP_NAME}:latest \
#     -t $BASE_URL/${APP_NAME}:${POINTER} .
#
#  docker push --all-tags $BASE_URL/${APP_NAME}

  # to stay within cloud shell limits, we can do the following:
  az acr build --image $BASE_URL/${APP_NAME}:latest \
    --image $BASE_URL/${APP_NAME}:${POINTER} \
    --build-arg JAR_FILE=${JAR_FILE} \
    --registry $3 --file ./build-scripts/basic/Dockerfile .

}

# Building the app
cd ..

echo "Building JAR files..."
mvn clean package -DskipTests -P with-kubernetes

echo "Building Docker images..."
build_basic ./math-service/target/math-service-${APP_VERSION}.jar provider-math $1
build_basic ./history-service/target/history-service-${APP_VERSION}.jar provider-history $1
build_basic ./exams-service/target/exams-service-${APP_VERSION}.jar provider-examinator $1
