# AI Shop - Blob Storage Service

This is a simple implementation of a Blob Storage Service. It is a RESTful service that allows you to upload file in Blob and get its URL back. It also provide to generate a SAS token URL with Read permission.

## Run locally

To run the Blob Storage Service locally, you need first to build the common module and then start the service. Follow the instructions below in the order they are presented:

1. Ensure that the Eurka Server is running. If not, [start the Eureka Server](../eureka-server/README.md)
2. Set the following environment variables:

    ```bash
    export STORAGE_ACCOUNT_NAME=<your-storage-account-name>
    export STORAGE_ACCOUNT_ENDPOINT=<your-storage-account-endpoint>
    export STORAGE_ACCOUNT_CONTAINER_NAME=<your-storage-account-container-name>
    ```

3. Start the Blob Storage Service in folder `src/blob-storage-service/`

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
    docker build -t $CONTAINER_REGISTRY_NAME.azurecr.io/blob-storage-service:$TAG .
    ```

4. Push the image

    ```bash
    docker push $CONTAINER_REGISTRY_NAME.azurecr.io/blob-storage-service:$TAG
    ```
