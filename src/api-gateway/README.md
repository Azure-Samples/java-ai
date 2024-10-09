# AI Shop - API Gateway

This service is responsible for orchestrating the calls to the other services. It is a Spring Boot application that uses Spring Cloud Eureka Server for service discovery.

## Run locally

To run the API Gateway locally, you need first to build the common module and then start the service. Follow the instructions below in the order they are presented:

1. Ensure that the Eurka Server is running. If not, [start the Eureka Server](../eureka-server/README.md)
2. Ensure that the Blob Storage Service is running. If not, [start the Blob Storage Service](../blob-storage-service/README.md)
3. Ensure that the AI Image Processing Service is running. If not, [start the AI Image Processing Service](../ai-image-processing-service/README.md)
4. Ensure that the Item Category Service is running. If not, [start the Item Category Service](../item-category-service/README.md)
5. Run the API Gateway

    ```bash
    ./mvnw spring-boot:run
    ```

## Build the image and push it to Azure Container Registry

1. Set the following environment variables:

    ```bash
    CONTAINER_REGISTRY_NAME=<your-acr-name>
    TAG=1.1.0
    ```

2. Login to Azure Container Registry

    ```bash
    az acr login --name $CONTAINER_REGISTRY_NAME
    ```

3. Build the image

    ```bash
    docker build -t $CONTAINER_REGISTRY_NAME.azurecr.io/api-gateway:$TAG .
    ```

4. Push the image

    ```bash
    docker push $CONTAINER_REGISTRY_NAME.azurecr.io/api-gateway:$TAG
    ```
