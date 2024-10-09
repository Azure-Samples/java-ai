# AI Shop - UI

This is the UI for the AI Shop project. It is a React application that uses the `api-gateway` to interact with the other services. The user can upload an image of the product they want to sell and see the generated information in a form.

## Run locally

To run the AI Shop UI locally, you need first to build the common module and then start the services. Follow the instructions below in the order they are presented:

1. Ensure that the Eureka Server is running. If not, [start the Eureka Server](../eureka-server/README.md)
2. Ensure that the Blob Storage Service is running. If not, [start the Blob Storage Service](../blob-storage-service/README.md)
3. Ensure that the AI Image Processing Service is running. If not, [start the AI Image Processing Service](../ai-image-processing-service/README.md)
4. Ensure that the Item Category Service is running. If not, [start the Item Category Service](../item-category-service/README.md)
5. Ensure that the API Gateway is running. If not, [start the API Gateway](../api-gateway/README.md)
6. Start the AI Shop UI

    ```bash
    npm install
    npm start
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
    docker build -t $CONTAINER_REGISTRY_NAME.azurecr.io/ai-shop-ui:$TAG .
    ```

4. Push the image

    ```bash
    docker push $CONTAINER_REGISTRY_NAME.azurecr.io/ai-shop-ui:$TAG
    ```
