targetScope = 'subscription'

@minLength(2)
@maxLength(32)
@description('Name of the the azd environment.')
param environmentName string = 'ai-workshop'

@minLength(2)
@description('Primary location for all resources.')
param location string

@description('Name of the the resource group. Default: rg-{environmentName}')
param resourceGroupName string = ''

param utcValue string = utcNow()
var abbrs = loadJsonContent('abbreviations.json')
var tags = {
  'azd-env-name': environmentName
  'utc-time': utcValue
}

@description('Organize resources in a resource group')
resource rg 'Microsoft.Resources/resourceGroups@2024-03-01' = {
  name: !empty(resourceGroupName) ? resourceGroupName : '${abbrs.resourcesResourceGroups}${environmentName}'
  location: location
  tags: tags
}

@description('Prepare Azure Container Registry for the images with UMI for AcrPull & AcrPush')
module containerRegistry '../deploy-container-registry.bicep' = {
  name: 'acr-${environmentName}'
  scope: rg
  params: {
    workloadName: environmentName
    environmentName: 'dev'
  }
}
var banner = 'mcr.microsoft.com/azurespringapps/default-banner:distroless-2024022107-66ea1a62-87936983'

module deploy 'deploy.bicep' = {
  name: 'deploy-${environmentName}'
  scope: rg
  params: {
    workloadName: environmentName
    environmentName: 'dev'
    containerRegistryName: containerRegistry.outputs.containerRegistryName
    apiGatewayImageName: banner
    imageProcessingImageName: banner
    blobStorageImageName: banner
    itemCategoryImageName: banner
    aiShopUiImageName: banner
  }
}

output resourceGroupName string = rg.name
output acrLoginServer string = containerRegistry.outputs.acrLoginServer
output azdProvisionTimestamp string = 'azd-${environmentName}-${utcValue}'
output environmentName string = 'dev'
