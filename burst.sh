#!/bin/bash

SVC_URL=$(kubectl get route/helloquarkus -o jsonpath="{.spec.port.targetPort}")://$(kubectl get route/helloquarkus -o jsonpath="{.spec.host}")

siege -r 1 -c 200 -d 2 -v $SVC_URL