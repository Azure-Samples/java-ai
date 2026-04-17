#!/usr/bin/env bash

cd src/java-ai-common/common
chmod +x ./mvnw
./mvnw clean install

cd ../../item-category-service
chmod +x ./mvnw
./mvnw clean package -DskipTests

cd ../ai-image-processing-service
chmod +x ./mvnw
./mvnw clean package -DskipTests

cd ../blob-storage-service
chmod +x ./mvnw
./mvnw clean package -DskipTests

cd ../api-gateway
chmod +x ./mvnw
./mvnw clean package -DskipTests
