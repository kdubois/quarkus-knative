# quarkus-knative

This demo shows how you can develop a Quarkus application and how to leverage Openshift Serverless (Knative) to rapidly scale
 the application from 0 when there's a burst of requests. 

## Requirements

* Linux (or Mac if you must :) ).  Windows will work as well but you'll need to use a different tool to burst requests. Eg. Jmeter should work.
* [siege](https://linux.die.net/man/1/siege) for the (kn)burst.sh scripts. eg. on Fedora: `dnf install siege`
* Java 11/17
* [Apache maven 3.5.3+](https://maven.apache.org/)
* [Quarkus CLI](https://quarkus.io/guides/cli-tooling) 
* the [oc](https://docs.openshift.com/container-platform/4.2/cli_reference/openshift_cli/getting-started-cli.html)
or [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/) cli tools. (they can be used interchangeably in this demo)
* An Openshift 4.2+ cluster (it may work on other Kubernetes clusters as well?)
* (OPTIONAL) [GraalVM CE](https://www.graalvm.org/) or [Mandrel](https://github.com/graalvm/mandrel)

## Local development

You can run the sample application with the following maven command.  
This will start a local runtime with virtually instantaneous hot reload of the application while you're making changes.

Compile and run the app in dev mode: `mvn compile quarkus:dev`

And see your changes on the fly with eg. `curl localhost:8080` or `./localpoller.sh`

`ctrl-c` when you're done

### Create Container Image & push to registry:

Log in to your registry (if you haven't already), eg:

```bash
podman login quay.io
```

Using the Quarkus CLI you can build the container image and push it in one go:

```bash
quarkus image push --also-build --native
```

### (If you are not using Openshift Sandbox) Install the Knative or Openshift Serverless Operator

If you're running Openshift, go to the OperatorHub in your Openshift cluster, and find the Openshift Serverless Operator, and install it.  Otherwise install the Knative Operator on your Kubernetes cluster using [these instructions]([https://knative.dev/docs/install/operator/knative-with-operators/).
Wait for the install to finish, then create a Knative Serving instance in the knative-serving namespace (which the operator should have created)
Optionally you can apply the following configmap if you want to set some aggressive defaults for your knative config:
`kubectl apply -f kubefiles/ConfigMap.yml -n knative-serving`

### Deploy application

Create a project if you haven't already, and then deploy Knative service (make sure you're logged in to the cluster)

* With Quarkus CLI:

```bash
quarkus deploy knative
```

* With Knative CLI (replace image with your image and adjust concurrency to your liking

```bash
kn service create quarked --image quay.io/kevindubois/quarked:1.0.0 --scale-min 0 --concurrency-limit 1 
```

* With kubectl (make sure to update knService_quay.yml with your image):

```bash
kubectl apply -f kubefiles/knService_quay.yml
```

### Start the siege

You can either just do one request at a time with the `./knpoller.sh` (boring)

Or you can launch a bunch of concurrent requests and really see the pods scale: `./knburst.sh`

If you want to compare with the non-knative deployment, use poller.sh and burst.sh

### Troubleshooting

If you're seeing requests bounce or error out, your nodes may not be able to handle the pressure from the siege command  
Try adjusting the siege parameters in the knburst.sh file; or configure knative's concurrency differently.

### Health / readiness / liveness checks

If you examined the pom.xml, you may have noticed the smallrye-health dependency.  Thanks to this, we have access to an automatic health api (check out the /q and /q/health endpoints)
