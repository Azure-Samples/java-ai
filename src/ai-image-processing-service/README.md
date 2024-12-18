# AI Shop - AI Image Processing Service

This service is responsible for processing images using AI models. It is a RESTful service that accepts an image URL and returns the information extracted from it:
- Item label
- Item brand
- Item model
- Item condition
- Item description
- Item price

The service uses the Azure OpenAI API GPT-4o to generate the information based on the image. The information is then returned to the caller.

## Run locally

To run the AI Image Processing Service locally, you need first to build the common module and then start the service. Follow the instructions below in the order they are presented:

1. [Build the common module](../java-ai-common/common/README.md)
2. Ensure that the Eurka Server is running. If not, [start the Eureka Server](../eureka-server/README.md)
3. Set the following environment variable. You will get the Azure OpenAI Endpoint in the Azure AI Foundry Portal https://ai.azure.com:

    ```bash
    export AZURE_OPENAI_ENDPOINT=<your-azure-openai-endpoint>
    ```

4. Start the AI Image Processing Service in folder `src/ai-image-processing-service/`

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
    docker build -t $CONTAINER_REGISTRY_NAME.azurecr.io/ai-image-processing-service:$TAG .
    ```

4. Push the image

    ```bash
    docker push $CONTAINER_REGISTRY_NAME.azurecr.io/ai-image-processing-service:$TAG
    ```
