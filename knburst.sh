#!/bin/bash

KSVC_URL="$(kubectl get ksvc quarked -o jsonpath='{.status.url}')"

siege -r 1 -c 255 -d 1 -v "$KSVC_URL"
