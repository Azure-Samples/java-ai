targetScope = 'resourceGroup'

/* -------------------------------------------------------------------------- */
/*                                 PARAMETERS                                 */
/* -------------------------------------------------------------------------- */

@description('The location of all resources.')
param location string = resourceGroup().location

@description('The name of the workload.')
param workloadName string = 'ai-shop'

@description('The tag of the image to deploy.')
param imageTag string = '1.1.0'

@description('The environment name.')
@allowed([
  'dev'
  'test'
  'prod'
])
param environmentName string = 'dev'

/* ----------------------------- Infrastructure ----------------------------- */

@description('The name of the key vault. Default to "kv<workloadName><environmentName><uniqueString(resourceGroup().id)>".')
param keyVaultName string = 'kv${replace(workloadName, '-', '')}${environmentName}${take(uniqueString(resourceGroup().id), 5)}'

@description('The name of the storage account. Default to "st<workloadName><environmentName><uniqueString(resourceGroup().id)>".')
param storageAccountName string = 'st${replace(workloadName, '-', '')}${environmentName}${take(uniqueString(resourceGroup().id), 5)}'

@description('The name of the storage account blob container. Default to "aishopinbox".')
param storageAccountBlobContainerName string = 'aishopinbox'

@description('The name of the Azure OpenAI resource. Default to "aoi-<workloadName>-<environmentName>".')
param azureOpenAIName string = 'aoi-${workloadName}-${environmentName}'

@description('The subdomain name of the Azure OpenAI resource. Default to "<workloadName><take(uniqueString(resourceGroup().id), 5)>".')
param azureOpenAISubDomainName string = '${replace(workloadName, '-', '')}${take(uniqueString(resourceGroup().id), 5)}'

@description('The name of the Log Analytics workspace. Default to "log-<workloadName>-<environmentName>".')
param logAnalyticsWorkspaceName string = 'log-${workloadName}-${environmentName}'

@description('The name of the container registry.')
param containerRegistryName string

@description('The name of the user-managed identity for ACR pull. Default to "umi-acr-pull-<containerRegistryName>".')
param acrPullUserManagedIdentityName string = 'umi-acr-pull-${containerRegistryName}'

@description('The name of the container apps environment. Default to "cae-<workloadName>-<environmentName>".')
param containerAppsEnvironmentName string = 'cae-${workloadName}-${environmentName}'

/* ----------------------------- Container Apps ----------------------------- */

@description('The name of the API gateway container app. Default to "ca-api-gateway-<environmentName>".')
param apiGatewayContainerAppName string = 'ca-api-gateway-${environmentName}'

@description('The name of the image processing service container app. Default to "ca-ai-image-process-serv-<environmentName>".')
param imageProcessingServiceContainerAppName string = 'ca-ai-image-process-serv-${environmentName}'

@description('The name of the blob storage service container app. Default to "ca-blob-storage-service-<environmentName>".')
param blobStorageServiceContainerAppName string = 'ca-blob-storage-service-${environmentName}'

@description('The name of the item category service container app. Default to "ca-item-category-<environmentName>".')
param itemCategoryServiceContainerAppName string = 'ca-item-category-${environmentName}'

@description('The name of the AI shop UI container app. Default to "ca-aishop-ui-<environmentName>".')
param aiShopUiContainerAppName string = 'ca-aishop-ui-${environmentName}'

@description('The image name of the api gateway app')
param apiGatewayImageName string

@description('The image name of the image process app')
param imageProcessingImageName string

@description('The image name of the blob storage app')
param blobStorageImageName string

@description('The image name of item category app')
param itemCategoryImageName string

@description('The image name of the api gateway app')
param aiShopUiImageName string

/* -------------------------------------------------------------------------- */
/*                                  VARIABLES                                 */
/* -------------------------------------------------------------------------- */

var acrPullRole = resourceId('Microsoft.Authorization/roleDefinitions', '7f951dda-4ed3-4680-a7ca-43fe172d538d')
var keyVaultSecretUserRole = resourceId(
  'Microsoft.Authorization/roleDefinitions',
  '4633458b-17de-408a-b874-0445c86b69e6'
)
var storageBlobDelegatorRole = resourceId(
  'Microsoft.Authorization/roleDefinitions',
  'db58b8e5-c6ad-4a2a-8342-4190687cbf4a'
)
var storageBlobDataContributorRole = resourceId(
  'Microsoft.Authorization/roleDefinitions',
  'ba92f5b4-2d11-453d-a403-e96b0029c9fe'
)
var cognitiveServicesOpenAIUserRole = resourceId(
  'Microsoft.Authorization/roleDefinitions',
  '5e0bd9bd-7b93-4f28-af87-19fc36ad61bd'
)

/* -------------------------------------------------------------------------- */
/*                                  RESOURCES                                 */
/* -------------------------------------------------------------------------- */

/* ----------------------- Container Apps Environment ----------------------- */

resource containerAppsEnvironment 'Microsoft.App/managedEnvironments@2024-03-01' = {
  name: containerAppsEnvironmentName
  location: location
  properties: {
    appLogsConfiguration: {
      destination: 'azure-monitor'
    }
    zoneRedundant: false
    kedaConfiguration: {}
    daprConfiguration: {}
    customDomainConfiguration: {}
    workloadProfiles: [
      {
        workloadProfileType: 'Consumption'
        name: 'Consumption'
      }
    ]
    peerAuthentication: {
      mtls: {
        enabled: false
      }
    }
    peerTrafficConfiguration: {
      encryption: {
        enabled: false
      }
    }
  }
}

resource containerAppsEnvironmentDiagnosticSettings 'Microsoft.Insights/diagnosticsettings@2021-05-01-preview' = {
  name: 'diagnostic-settings'
  properties: {
    workspaceId: logAnalyticsWorkspace.id
    metrics: [
      {
        category: 'AllMetrics'
        enabled: true
      }
    ]
    logs: [
      {
        categoryGroup: 'allLogs'
        enabled: true
      }
    ]
  }
  scope: containerAppsEnvironment
}

resource eurekaServer 'Microsoft.App/managedEnvironments/javaComponents@2024-02-02-preview' = {
  parent: containerAppsEnvironment
  name: 'eureka'
  properties: {
    componentType: 'SpringCloudEureka'
    ingress: {}
    configurations: [
      {
        propertyName: 'eureka.server.renewal-percent-threshold'
        value: '0.85'
      }
      {
        propertyName: 'eureka.server.eviction-interval-timer-in-ms'
        value: '5000'
      }
    ]
  }
}

resource springAdmin 'Microsoft.App/managedEnvironments/javaComponents@2024-02-02-preview' = {
  parent: containerAppsEnvironment
  name: 'spring-admin'
  properties: {
    componentType: 'SpringBootAdmin'
    ingress: {}
    configurations: []
  }
}

/* --------------------------- Container Registry --------------------------- */

resource containerRegistry 'Microsoft.ContainerRegistry/registries@2023-11-01-preview' existing = {
  name: containerRegistryName
}

resource acrPullUserManagedIdentity 'Microsoft.ManagedIdentity/userAssignedIdentities@2023-07-31-preview' = {
  name: acrPullUserManagedIdentityName
  location: location
}

resource containerRegistryAcrPullRoleAssignment 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  name: guid(containerRegistry.id, acrPullUserManagedIdentity.id, acrPullRole)
  scope: containerRegistry
  properties: {
    principalId: acrPullUserManagedIdentity.properties.principalId
    principalType: 'ServicePrincipal'
    roleDefinitionId: acrPullRole
  }
}

/* -------------------------------- Key Vault ------------------------------- */

resource keyVault 'Microsoft.KeyVault/vaults@2024-04-01-preview' = {
  name: keyVaultName
  location: location
  properties: {
    sku: {
      family: 'A'
      name: 'standard'
    }
    tenantId: subscription().tenantId
    networkAcls: {
      bypass: 'None'
      defaultAction: 'Allow'
      ipRules: []
      virtualNetworkRules: []
    }
    accessPolicies: []
    enabledForDeployment: false
    enabledForDiskEncryption: false
    enabledForTemplateDeployment: false
    enableSoftDelete: true
    softDeleteRetentionInDays: 90
    enableRbacAuthorization: true
  }
}

/* ------------------------------- Monitoring ------------------------------- */

resource logAnalyticsWorkspace 'Microsoft.OperationalInsights/workspaces@2023-09-01' = {
  name: logAnalyticsWorkspaceName
  location: location
  properties: {
    sku: {
      name: 'PerGB2018'
    }
    retentionInDays: 30
    features: {
      legacy: 0
      searchVersion: 1
      enableLogAccessUsingOnlyResourcePermissions: true
    }
    workspaceCapping: {
      dailyQuotaGb: -1
    }
    publicNetworkAccessForIngestion: 'Enabled'
    publicNetworkAccessForQuery: 'Enabled'
  }
}

/* ------------------------------ Azure OpenAI ------------------------------ */

resource azureOpenAI 'Microsoft.CognitiveServices/accounts@2024-04-01-preview' = {
  name: azureOpenAIName
  location: location
  sku: {
    name: 'S0'
  }
  kind: 'OpenAI'
  properties: {
    customSubDomainName: azureOpenAISubDomainName
    publicNetworkAccess: 'Enabled'
  }
}

resource gpt4oDeployment 'Microsoft.CognitiveServices/accounts/deployments@2024-04-01-preview' = {
  parent: azureOpenAI
  name: 'gpt-4o'
  sku: {
    name: 'GlobalStandard'
    capacity: 30
  }
  properties: {
    model: {
      format: 'OpenAI'
      name: 'gpt-4o'
      version: '2024-05-13'
    }
    versionUpgradeOption: 'OnceCurrentVersionExpired'
    currentCapacity: 450
    raiPolicyName: 'Microsoft.DefaultV2'
  }
}

resource azureOpenAISecret 'Microsoft.KeyVault/vaults/secrets@2024-04-01-preview' = {
  parent: keyVault
  name: 'azure-openai-key'
  properties: {
    attributes: {
      enabled: true
    }
    value: azureOpenAI.listKeys().key1
  }
}

/* --------------------------------- Storage -------------------------------- */

resource storageAccount 'Microsoft.Storage/storageAccounts@2023-05-01' = {
  name: storageAccountName
  location: location
  sku: {
    name: 'Standard_LRS'
  }
  kind: 'StorageV2'
  properties: {
    publicNetworkAccess: 'Enabled'
    allowBlobPublicAccess: false
    allowSharedKeyAccess: true
    largeFileSharesState: 'Enabled'
    supportsHttpsTrafficOnly: true
    accessTier: 'Hot'
  }
}

resource storageAccountBlobService 'Microsoft.Storage/storageAccounts/blobServices@2023-05-01' = {
  parent: storageAccount
  name: 'default'
  properties: {}
}

resource storageAccountBlobContainer 'Microsoft.Storage/storageAccounts/blobServices/containers@2023-05-01' = {
  parent: storageAccountBlobService
  name: storageAccountBlobContainerName
}

/* ----------------------------- Container Apps ----------------------------- */

resource aiImageProcessingServiceContainerApp 'Microsoft.App/containerApps@2024-02-02-preview' = {
  name: imageProcessingServiceContainerAppName
  location: location
  identity: {
    type: 'SystemAssigned, UserAssigned'
    userAssignedIdentities: {
      '${acrPullUserManagedIdentity.id}': {}
    }
  }
  properties: {
    managedEnvironmentId: containerAppsEnvironment.id
    workloadProfileName: 'Consumption'
    configuration: {
      secrets: []
      activeRevisionsMode: 'Single'
      ingress: {
        external: false
        targetPort: 8083
        exposedPort: 0
        transport: 'Auto'
        allowInsecure: false
      }
      registries: [
        {
          server: containerRegistry.properties.loginServer
          identity: acrPullUserManagedIdentity.id
        }
      ]
      runtime: {
        java: {
          enableMetrics: true
        }
      }
    }
    template: {
      containers: [
        {
          image: !empty(imageProcessingImageName) ? imageProcessingImageName: '${containerRegistry.properties.loginServer}/ai-image-processing-service:${imageTag}'
          name: 'ai-image-processing-service'
          env: [
            {
              name: 'AZURE_OPENAI_ENDPOINT'
              value: azureOpenAI.properties.endpoint
            }
          ]
          resources: {
            cpu: json('0.5')
            memory: '1Gi'
          }
          probes: []
        }
      ]
      scale: {
        minReplicas: 1
        maxReplicas: 10
      }
      volumes: []
      serviceBinds: [
        {
          serviceId: eurekaServer.id
        }
        {
          serviceId: springAdmin.id
        }
      ]
    }
  }
}

resource cognitiveServicesOpenAIUserRoleAssignment 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  name: guid(azureOpenAI.id, gpt4oDeployment.name, cognitiveServicesOpenAIUserRole)
  scope: azureOpenAI
  properties: {
    principalId: aiImageProcessingServiceContainerApp.identity.principalId
    principalType: 'ServicePrincipal'
    roleDefinitionId: cognitiveServicesOpenAIUserRole
  }
}

resource apiGatewayContainerApp 'Microsoft.App/containerapps@2024-02-02-preview' = {
  name: apiGatewayContainerAppName
  location: location
  identity: {
    type: 'UserAssigned'
    userAssignedIdentities: {
      '${acrPullUserManagedIdentity.id}': {}
    }
  }
  properties: {
    managedEnvironmentId: containerAppsEnvironment.id
    workloadProfileName: 'Consumption'
    configuration: {
      secrets: []
      activeRevisionsMode: 'Single'
      ingress: {
        external: true
        targetPort: 8081
        exposedPort: 0
        transport: 'Auto'
        allowInsecure: false
        corsPolicy: {
          allowedOrigins: [
            '*'
          ]
          allowedHeaders: [
            '*'
          ]
          maxAge: 0
          allowCredentials: false
        }
      }
      registries: [
        {
          server: containerRegistry.properties.loginServer
          identity: acrPullUserManagedIdentity.id
        }
      ]
      runtime: {
        java: {
          enableMetrics: true
        }
      }
      maxInactiveRevisions: 100
    }
    template: {
      containers: [
        {
          image: !empty(apiGatewayImageName) ? apiGatewayImageName : '${containerRegistry.properties.loginServer}/api-gateway:${imageTag}'
          name: 'api-gateway'
          resources: {
            cpu: json('0.5')
            memory: '1Gi'
          }
          probes: []
        }
      ]
      scale: {
        minReplicas: 1
        maxReplicas: 10
      }
      volumes: []
      serviceBinds: [
        {
          serviceId: eurekaServer.id
        }
        {
          serviceId: springAdmin.id
        }
      ]
    }
  }
}

resource blobStorageServiceContainerApp 'Microsoft.App/containerapps@2024-02-02-preview' = {
  name: blobStorageServiceContainerAppName
  location: location
  identity: {
    type: 'SystemAssigned, UserAssigned'
    userAssignedIdentities: {
      '${acrPullUserManagedIdentity.id}': {}
    }
  }
  properties: {
    managedEnvironmentId: containerAppsEnvironment.id
    workloadProfileName: 'Consumption'
    configuration: {
      secrets: []
      activeRevisionsMode: 'Single'
      ingress: {
        external: false
        targetPort: 8082
        exposedPort: 0
        transport: 'Auto'
        allowInsecure: false
      }
      registries: [
        {
          server: containerRegistry.properties.loginServer
          identity: acrPullUserManagedIdentity.id
        }
      ]
      runtime: {
        java: {
          enableMetrics: true
        }
      }
    }
    template: {
      containers: [
        {
          image: !empty(blobStorageImageName) ? blobStorageImageName : '${containerRegistry.properties.loginServer}/blob-storage-service:${imageTag}'
          name: 'blob-storage-service'
          env: [
            {
              name: 'STORAGE_ACCOUNT_NAME'
              value: storageAccount.name
            }
            {
              name: 'STORAGE_ACCOUNT_ENDPOINT'
              value: storageAccount.properties.primaryEndpoints.blob
            }
            {
              name: 'STORAGE_ACCOUNT_CONTAINER_NAME'
              value: storageAccountBlobContainer.name
            }
          ]
          resources: {
            cpu: json('0.5')
            memory: '1Gi'
          }
          probes: []
        }
      ]
      scale: {
        minReplicas: 1
        maxReplicas: 10
      }
      volumes: []
      serviceBinds: [
        {
          serviceId: eurekaServer.id
        }
        {
          serviceId: springAdmin.id
        }
      ]
    }
  }
}

resource storageBlobDataContributorRoleAssignment 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  name: guid(
    storageAccount.id,
    storageAccountBlobContainer.id,
    keyVaultSecretUserRole,
    blobStorageServiceContainerApp.id
  )
  scope: storageAccountBlobContainer
  properties: {
    principalId: blobStorageServiceContainerApp.identity.principalId
    principalType: 'ServicePrincipal'
    roleDefinitionId: storageBlobDataContributorRole
  }
}

resource storageBlobDelegatorRoleAssignment 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  name: guid(storageAccount.id, storageBlobDelegatorRole, blobStorageServiceContainerApp.id)
  scope: storageAccount
  properties: {
    principalId: blobStorageServiceContainerApp.identity.principalId
    principalType: 'ServicePrincipal'
    roleDefinitionId: storageBlobDelegatorRole
  }
}

resource itemCategoryServiceContainerApps 'Microsoft.App/containerApps@2024-02-02-preview' = {
  name: itemCategoryServiceContainerAppName
  location: location
  identity: {
    type: 'SystemAssigned, UserAssigned'
    userAssignedIdentities: {
      '${acrPullUserManagedIdentity.id}': {}
    }
  }
  properties: {
    managedEnvironmentId: containerAppsEnvironment.id
    workloadProfileName: 'Consumption'
    configuration: {
      secrets: [
        {
          name: 'azure-openai-key'
          keyVaultUrl: azureOpenAISecret.properties.secretUri
          identity: 'system'
        }
      ]
      activeRevisionsMode: 'Single'
      ingress: {
        external: false
        targetPort: 8084
        exposedPort: 0
        transport: 'Auto'
        allowInsecure: false
      }
      registries: [
        {
          server: containerRegistry.properties.loginServer
          identity: acrPullUserManagedIdentity.id
        }
      ]
      runtime: {
        java: {
          enableMetrics: true
        }
      }
    }
    template: {
      containers: [
        {
          image: !empty(itemCategoryImageName) ? itemCategoryImageName: '${containerRegistry.properties.loginServer}/item-category-service:${imageTag}'
          name: 'item-category-service'
          env: [
            {
              name: 'AZURE_OPENAI_API_KEY'
              secretRef: 'azure-openai-key'
            }
            {
              name: 'AZURE_OPENAI_ENDPOINT'
              value: azureOpenAI.properties.endpoint
            }
            {
              name: 'AZURE_OPENAI_DEPLOYMENT_NAME'
              value: gpt4oDeployment.name
            }
          ]
          resources: {
            cpu: json('0.5')
            memory: '1Gi'
          }
          probes: []
        }
      ]
      scale: {
        minReplicas: 1
        maxReplicas: 10
      }
      volumes: []
      serviceBinds: [
        {
          serviceId: eurekaServer.id
        }
        {
          serviceId: springAdmin.id
        }
      ]
    }
  }
}

resource azureOpenAISecretSecretUserRoleAssignment 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  name: guid(azureOpenAISecret.id, keyVault.id, keyVaultSecretUserRole, itemCategoryServiceContainerApps.id)
  scope: azureOpenAISecret
  properties: {
    principalId: itemCategoryServiceContainerApps.identity.principalId
    principalType: 'ServicePrincipal'
    roleDefinitionId: keyVaultSecretUserRole
  }
}

resource containerApp 'Microsoft.App/containerApps@2024-02-02-preview' = {
  name: aiShopUiContainerAppName
  location: location
  identity: {
    type: 'SystemAssigned, UserAssigned'
    userAssignedIdentities: {
      '${acrPullUserManagedIdentity.id}': {}
    }
  }
  properties: {
    managedEnvironmentId: containerAppsEnvironment.id
    configuration: {
      ingress: {
        external: true
        targetPort: 80
      }
      registries: [
        {
          server: containerRegistry.properties.loginServer
          identity: acrPullUserManagedIdentity.id
        }
      ]
    }
    template: {
      containers: [
        {
          image: !empty(aiShopUiImageName) ? aiShopUiImageName : '${containerRegistry.properties.loginServer}/ai-shop-ui:${imageTag}'
          name: 'ai-shop-ui'
          resources: {
            memory: '2Gi'
            cpu: 1
          }
          env: [
            {
              name: 'REACT_APP_API_URL'
              value: 'https://${apiGatewayContainerApp.properties.configuration.ingress.fqdn}/item-info'
            }
          ]
        }
      ]
    }
  }
}

/* -------------------------------------------------------------------------- */
/*                                   OUTPUTS                                  */
/* -------------------------------------------------------------------------- */

@description('The name of the API gateway container app.')
output apiGatewayContainerAppName string = apiGatewayContainerApp.name

@description('The name of the image processing service container app.')
output imageProcessingServiceContainerAppName string = aiImageProcessingServiceContainerApp.name

@description('The name of the blob storage service container app.')
output blobStorageServiceContainerAppName string = blobStorageServiceContainerApp.name

@description('The name of the item category service container app.')
output itemCategoryServiceContainerAppName string = itemCategoryServiceContainerApps.name
