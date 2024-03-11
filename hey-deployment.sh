URL=$(oc get route quarked --output jsonpath={.spec.host})
hey -n 2000 -c 300 https://${URL}/spin
