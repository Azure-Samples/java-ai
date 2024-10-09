# Item Category Service

This service is responsible for providing the categories of the items that can be sold in the shop. It is a RESTful service that returns the categories based on the product description. The service uses the LangChain4J library to generate the categories based on the product information.

## Run locally

To run the Item Category Service locally, you need first to build the common module and then start the service. Follow the instructions below in the order they are presented:

1. [Build the common module](../java-ai-common/common/README.md)
2. Ensure that the Eurka Server is running. If not, [start the Eureka Server](../eureka-server/README.md)
3. Set the following environment variables:

    ```bash
    export AZURE_OPENAI_API_KEY=<your-azure-openai-api-key>
    export AZURE_OPENAI_ENDPOINT=<your-azure-openai-endpoint>
    export AZURE_OPENAI_DEPLOYMENT_NAME=<your-azure-openai-deployment-name>
    ```
4. Start the Item Category Service

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
    docker build -t $CONTAINER_REGISTRY_NAME.azurecr.io/item-category-service:$TAG .
    ```

4. Push the image

    ```bash
    docker push $CONTAINER_REGISTRY_NAME.azurecr.io/item-category-service:$TAG
    ```
