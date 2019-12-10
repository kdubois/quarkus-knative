#!/bin/bash

KSVC_URL="$(kubectl get ksvc helloquarkusknative -o jsonpath='{.status.url}')"

siege -r 3 -c 250 -d 1 -v "$KSVC_URL"
