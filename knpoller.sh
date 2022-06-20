#!/bin/bash

KSVC_URL="$(kubectl -n serverless get ksvc quarked -o jsonpath='{.status.url}')"

while true; do
  curl $KSVC_URL
  sleep .3
done
