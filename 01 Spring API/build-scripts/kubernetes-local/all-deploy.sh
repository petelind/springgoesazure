#!/bin/bash
kubectl create namespace green
kubectl config set-context --current --namespace=green
kubectl apply -f deployment.yaml
wait 5
kubectl port-forward service/endpoint-examinator 8080:8080

