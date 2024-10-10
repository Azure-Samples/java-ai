LOCATION=swedencentral
ENVIRONMENT_NAME=dev
WORKLOAD_NAME=aishop
RESOURCE_GROUP_NAME=rg-$WORKLOAD_NAME-$ENVIRONMENT_NAME

# Create a resource group
az group create --name $RESOURCE_GROUP_NAME --location $LOCATION

# Create an Azure Container Registry
CONTAINER_REGISTRY_DEPLOYMENT_NAME=$WORKLOAD_NAME-$ENVIRONMENT_NAME-acr-deployment
az deployment group create \
  --name $CONTAINER_REGISTRY_DEPLOYMENT_NAME \
  --resource-group $RESOURCE_GROUP_NAME \
  --template-file infra/deploy-container-registry.bicep \
  --parameters workloadName=$WORKLOAD_NAME \
  --parameters environmentName=$ENVIRONMENT_NAME

# Build and push images to the Azure Container Registry
CONTAINER_REGISTRY_NAME="$(az deployment group show --name $CONTAINER_REGISTRY_DEPLOYMENT_NAME --resource-group $RESOURCE_GROUP_NAME --query properties.outputs.containerRegistryName.value -o tsv | tr -d '\r' | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"
TAG=1.1.0

az acr login --name $CONTAINER_REGISTRY_NAME

cd src/java-ai-common/common
./mvnw clean install

cd ../../item-category-service
./mvnw clean package
docker build -t $CONTAINER_REGISTRY_NAME.azurecr.io/item-category-service:$TAG .
docker push $CONTAINER_REGISTRY_NAME.azurecr.io/item-category-service:$TAG

cd ../ai-image-processing-service
./mvnw clean package
docker build -t $CONTAINER_REGISTRY_NAME.azurecr.io/ai-image-processing-service:$TAG .
docker push $CONTAINER_REGISTRY_NAME.azurecr.io/ai-image-processing-service:$TAG

cd ../blob-storage-service
./mvnw clean package
docker build -t $CONTAINER_REGISTRY_NAME.azurecr.io/blob-storage-service:$TAG .
docker push $CONTAINER_REGISTRY_NAME.azurecr.io/blob-storage-service:$TAG

cd ../api-gateway
./mvnw clean package
docker build -t $CONTAINER_REGISTRY_NAME.azurecr.io/api-gateway:$TAG .
docker push $CONTAINER_REGISTRY_NAME.azurecr.io/api-gateway:$TAG

cd ../ai-shop-ui
docker build -t $CONTAINER_REGISTRY_NAME.azurecr.io/ai-shop-ui:$TAG .
docker push $CONTAINER_REGISTRY_NAME.azurecr.io/ai-shop-ui:$TAG


# Deploy the rest of the infrastructure
cd ../..
DEPLOYMENT_NAME=$WORKLOAD_NAME-$ENVIRONMENT_NAME-deployment
az deployment group create \
  --name $DEPLOYMENT_NAME \
  --resource-group $RESOURCE_GROUP_NAME \
  --template-file infra/deploy.bicep \
  --parameters imageTag=$TAG \
  --parameters workloadName=$WORKLOAD_NAME \
  --parameters environmentName=$ENVIRONMENT_NAME \
  --parameters containerRegistryName="${CONTAINER_REGISTRY_NAME}"

# Get the name of each Container App
API_GATEWAY_NAME="$(az deployment group show --name $DEPLOYMENT_NAME --resource-group $RESOURCE_GROUP_NAME --query properties.outputs.apiGatewayContainerAppName.value -o tsv | tr -d '\r' | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"
AI_IMAGE_PROCESSING_SERVICE_NAME="$(az deployment group show --name $DEPLOYMENT_NAME --resource-group $RESOURCE_GROUP_NAME --query properties.outputs.imageProcessingServiceContainerAppName.value -o tsv | tr -d '\r' | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"
BLOB_STORAGE_SERVICE_NAME="$(az deployment group show --name $DEPLOYMENT_NAME --resource-group $RESOURCE_GROUP_NAME --query properties.outputs.imageProcessingServiceContainerAppName.value -o tsv | tr -d '\r' | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"
ITEM_CATEGORY_SERVICE_NAME="$(az deployment group show --name $DEPLOYMENT_NAME --resource-group $RESOURCE_GROUP_NAME --query properties.outputs.imageProcessingServiceContainerAppName.value -o tsv | tr -d '\r' | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"

# Update the runtime of each Container App to Java
az containerapp update --name $API_GATEWAY_NAME --resource-group $RESOURCE_GROUP_NAME --runtime java --enable-java-metrics
az containerapp update --name $AI_IMAGE_PROCESSING_SERVICE_NAME --resource-group $RESOURCE_GROUP_NAME --runtime java --enable-java-metrics
az containerapp update --name $BLOB_STORAGE_SERVICE_NAME --resource-group $RESOURCE_GROUP_NAME --runtime java --enable-java-metrics
az containerapp update --name $ITEM_CATEGORY_SERVICE_NAME --resource-group $RESOURCE_GROUP_NAME --runtime java --enable-java-metrics
