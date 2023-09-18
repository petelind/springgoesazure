#!/bin/bash

# set app version to 0.0.1-SNAPSHOT; just so we can see what we have built
export APP_VERSION=0.0.1-SNAPSHOT
export RGNAME=AZSpringAPI2
## Todo: add few random numbers to registry name to make it globally unique
export ACRNAME=azspringapiacr2

# Deploy a cluster with an AppGW Ingress Controller for exam API access
az group create --name $RGNAME --location centralus
az config set defaults.group=$RGNAME

## Create Azure Container Regisry with admin enabled
az acr create --resource-group $RGNAME --name $ACRNAME --sku Basic --admin-enabled true

## Get credetials for ACR to push containers there and store the in env variable for later use
ACRPASSWORD=$(az acr credential show --resource-group $RGNAME --name $ACRNAME --output json --query 'passwords[0].value')
export ACRPASSWORD=${ACRPASSWORD//\"/}

## Login to ACR and push images tagged with ACR name
docker login $ACRNAME.azurecr.io --username $ACRNAME --password $ACRPASSWORD
../build-scripts/build-images-kubernetes-acr.sh $ACRNAME $ACRPASSWORD

##Create a cluster with the AppGW Ingress addon
az aks create \
    --resource-group $RGNAME  \
    --name examcluster \
    --network-plugin azure \
    --enable-managed-identity \
    --enable-addon ingress-appgw \
    --appgw-name AZSpringAPI-gw \
    --appgw-subnet-cidr "10.225.0.0/16" \
    --attach-acr $ACRNAME \
    --generate-ssh-keys


# if you want to retain yours: --use-ssh-key ~/.ssh/id_rsa.pub
##Get the kubeconfig to log into the cluster
az aks get-credentials --resource-group $RGNAME --name examcluster

## Make this context the current context
kubectl config use-context examcluster

## Create a namespace for our application
kubectl create namespace green
kubectl config set-context --current --namespace=green

# if you ever need to attach ACR to live AKS cluster:
# az aks update -n examcluster  -g $RGNAME --attach-acr $ACRNAME


#Create a Deployment, ClusterIP Service, and Ingress to access our application via AppGW
#Look into the file for examples of deployments, services, configmaps etc.
sed -i "s|image: azspringapiacr1.azurecr.io|image: $ACRNAME.azurecr.io|g" deployment-aks.yaml


# alternate way to expose is via l3 ingress - LoadBalancer
#kubectl expose deployment backend-examinator-deployment --name=l3balancer --type=LoadBalancer --port=80 --target-port 8080


##Our current application is running on three pods
kubectl get pods -o wide


##Wait for the IP address to populate for the ingress...this can take a minute or two to update.
kubectl get ingress --watch


##Access the application via the exposed ingress on the public IP
export INGRESSIP=$(kubectl get ingress -o jsonpath='{ .items[].status.loadBalancer.ingress[].ip }')
echo $INGRESSIP
curl http://$INGRESSIP

# Local troubleshooting:
# kubectl run -it --rm --image=curlimages/curl curly -- sh

##Our ingress controller is a pod running in the cluster...
###monitoring for ingress resources and updating the configuration of the AppGW
kubectl describe pods -n kube-system -l app=ingress-appgw | more


#Open the Azure Portal, search for examcluster and go to the Managed Cluster Resource Group
##...and find the deployed AppGW
#1. On Backend Pools you'll find a pool pointing the to nodes in the cluster
#2. Traffic is sent from the AppGW straight to the Pod IPs on Azure CNI since they're all on the same subnet


#Clean up from this demo
#az group delete --name AZSpringAPI
#kubectl config delete-context examcluster
