targetScope = 'subscription'

@minLength(1)
@maxLength(64)
@description('Name of the the environment which is used to generate a short unique hash used in all resources.')
param environmentName string

@minLength(1)
@description('Primary location for all resources')
param location string

@description('Relative Path of ASA Jar')
param relativePath string

var abbrs = loadJsonContent('./abbreviations.json')
var resourceToken = toLower(uniqueString(subscription().id, environmentName, location))
var storageAccountName = '${abbrs.storageStorageAccounts}${resourceToken}'
var fileShareName = 'vectorstore'
var storageMountName = 'vectorstore'
var cognitiveAccountName = '${abbrs.cognitiveServicesAccounts}${resourceToken}'
var asaInstanceName = '${abbrs.springApps}${resourceToken}'
var asaManagedEnvironmentName = '${abbrs.appContainerAppsManagedEnvironment}${resourceToken}'
var appName = 'spring-chatgpt-sample-webapi'
var tags = {
  'azd-env-name': environmentName
  'spring-cloud-azure': 'true'
}

resource rg 'Microsoft.Resources/resourceGroups@2021-04-01' = {
  name: '${abbrs.resourcesResourceGroups}${environmentName}-${resourceToken}'
  location: location
  tags: tags
}

module storage 'modules/storage/storage.bicep' = {
  name: '${deployment().name}--storage'
  scope: resourceGroup(rg.name)
  params: {
    location: location
    tags: tags
    storageAccountName: storageAccountName
	fileShareName: fileShareName
  }
}

module cognitive 'modules/cognitive/cognitive.bicep' = {
  name: '${deployment().name}--cog'
  scope: resourceGroup(rg.name)
  params: {
    location: location
    tags: tags
    accountName: cognitiveAccountName
    deployments: [
	  {
		name: 'gpt-35-turbo'
		model: {
		  format: 'OpenAI'
		  name: 'gpt-35-turbo'
		  version: '0301'
		}
		capacity: 30
	  }
	  {
		name: 'text-embedding-ada-002'
		model: {
		  format: 'OpenAI'
		  name: 'text-embedding-ada-002'
		  version: '2'
		}
		capacity: 30
	  }
	]
  }
}

module springApps 'modules/springapps/springapps.bicep' = {
  name: '${deployment().name}--asa'
  scope: resourceGroup(rg.name)
  params: {
    location: location
    appName: appName
    tags: tags
    asaInstanceName: asaInstanceName
	asaManagedEnvironmentName: asaManagedEnvironmentName
    relativePath: relativePath
    storageAccountName: storageAccountName
	fileShareName: fileShareName
	storageMountName: storageMountName
	environmentVariables: {
	  AZURE_OPENAI_ENDPOINT: cognitive.outputs.endpoint
	  AZURE_OPENAI_APIKEY: cognitive.outputs.key
	  AZURE_OPENAI_CHATDEPLOYMENTID: 'gpt-35-turbo'
	  AZURE_OPENAI_EMBEDDINGDEPLOYMENTID: 'text-embedding-ada-002'
	  VECTORSTORE_FILE: '/opt/spring-chatgpt-sample/doc_store.json'
	}
  }
  dependsOn: [
    storage
  ]
}

output STORAGE_ACCOUNT_NAME string = '${storageAccountName}'
output STORAGE_ACCOUNT_KEY string = '${storage.outputs.storageAccountKey}'