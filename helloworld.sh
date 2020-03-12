#!/bin/bash

kn service create demo --image quay.io/kevindubois/quarked:1.0.0 --limits-memory=70m --limits-cpu=60Mi

KSVC_URL="$(kubectl get ksvc demo -o jsonpath='{.status.url}')"

siege -r 3 -c 250 -d 1 -v "$KSVC_URL"