#!/bin/bash
kubectl create namespace green
kubectl config set-context --current --namespace=green
kubectl apply -f deployment.yaml
kubectl port-forward service/challenges-provider 8080:8080
