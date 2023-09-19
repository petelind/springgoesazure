export RGNAME=examdirect
export YOURNAME=denis

# Create API Management instance
az apim create --name examdirectt --resource-group $RGNAME \
  --publisher-name Contoso --publisher-email admin@nowhere.com

# Create an Azure API Management service
az apim create --name examdirect-$YOURNAME --resource-group $RGNAME --location southindia --publisher-email Denis@NYU --publisher-name Denis@NYU.com

# Import the OpenAPI spec into your API Management service
az apim api import --name exams --resource-group $RGNAME --service-name examdirect-$YOURNAME --specification-format OpenApi --specification-url "http://prod.phainestai.net/" --path "/v3/api-docs"


# Publish the API
az apim api update --name exams --resource-group $RGNAME --service-name examdirect-$YOURNAME --set path='/' --set isCurrent='true'
