# Java AI - AI Shop

This is a simple AI Shop application that demonstrates how to use Azure OpenAI with Java to create a sample second hand shop. It uses the multi-modal capabilities of the OpenAI API to generate a description of the product based on an image. Instead of filling out a form, the users can simply upload an image of the product they want to sell and the AI will generate all the necessary information and fill the form for them.

The functional architecture of the application is as follows:
- `src/ai-shop-ui`: The user interface of the application. It is a simple React application that allows the user to upload an image and see the generated information in a form.
- `src/api-gateway`: The API Gateway of the application. It is a Spring Boot application that orchestrates the calls to the other services. It uses Spring Cloud Eureka Server for service discovery.
- `src/eureka-server`: The Eureka Server of the application. It is simple Eureka Server that is only required for local development.
- `src/ai-image-processing-service`: The AI Image Processing Service of the application. It is a Spring Boot application that uses `Spring AI` to generate the product information based on the image.
- `src/blob-storage-service`: The Blob Storage Service of the application. It is a Spring Boot application that uses `Spring Cloud Azure` to store the images in Azure Blob Storage. It also provide the generation of the image URL with a SAS token for the `ai-image-processing-service`.
- `src/item-category-service`: The Item Category Service of the application. It is a Spring Boot application that provides the categories of the items that can be sold in the shop. It uses `Spring Data JPA` to store the categories in a `H2` database. It is infused with AI and uses `LangChain4J` to generate the categories based on the product description.
- `src/java-ai-common`: A common module that is used by all the services. It contains the common DTOs.

![Functional Architecture](./media/functional-architecture.png)

## Run locally

To run the AI Shop locally, you need first to build the common module and then start the services. Follow the instructions below in the order they are presented:

1. [Build the common module](src/java-ai-common/common/README.md)
2. [Start the Eureka Server](src/eureka-server/README.md)
3. [Start the Blob Storage Service](src/blob-storage-service/README.md)
4. [Start the AI Image Processing Service](src/ai-image-processing-service/README.md)
5. [Start the Item Category Service](src/item-category-service/README.md)
6. [Start the API Gateway](src/api-gateway/README.md)
7. [Start the AI Shop UI](src/ai-shop-ui/README.md)

## Deploy to Azure

The following resources will be created as represented in the diagram below:
- Resource Group
- Azure Container Registry
- Azure Container Apps Environment
- Azure Blob Storage Account
- Azure Key Vault for the `item-category-service` Azure OpenAI API Key secret
- User-assigned Managed Identity to pull the images from the Azure Container Registry
- Azure OpenAI
- Azure Container Apps
- Azure Logs Analytics Workspace for monitoring

![Azure Architecture](./media/azure-architecture.png)

### Fast deployment

To deploy the AI Shop to Azure, you need only to use the provided `deploy.sh` script. The script will create the necessary resources in Azure and deploy the services to Azure Container Apps. Follow the instructions below:

1. Set the following environment variables (optional):

    ```bash
    export LOCATION=<your-location>
    export RESOURCE_GROUP_NAME=<your-resource-group-name>
    export WORKLOAD_NAME=<your-workload-name>
    ```

2. Run the `deploy.sh` script:

    ```bash
    bash deploy.sh
    ```

The deployment script has 3 main steps:
1. Create the resource group and the Container Registry
2. Build the images and push them to the Container Registry
3. Deploy the rest of the infrastructure and the services to Azure Container Apps
