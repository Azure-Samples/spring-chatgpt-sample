param accountName string
param tags object
param location string = resourceGroup().location
param deployments array = []

resource account 'Microsoft.CognitiveServices/accounts@2022-12-01' = {
  name: accountName
  location: location
  tags: tags
  kind: 'OpenAI'
  sku: {
    name: 'S0'
  }
  properties: {
    customSubDomainName: accountName
  }
}

@batchSize(1)
resource embedding 'Microsoft.CognitiveServices/accounts/deployments@2022-12-01' = [for deployment in deployments: {
  parent: account
  name: deployment.name
  properties: {
    model: deployment.model
    scaleSettings: deployment.scaleSettings
  }
}]

output endpoint string = account.properties.endpoint
output key string = account.listKeys().key1