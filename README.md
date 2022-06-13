# quarkus-knative
This demo shows how you can develop a Quarkus application and how to leverage Openshift Serverless (Knative) to rapidly scale 
 the application from 0 when there's a burst of requests.  Based on Burr Sutter's sidebyside demo.

### Requirements

* Linux (or Mac if you must :) )
* [siege](https://linux.die.net/man/1/siege) for the (kn)burst.sh scripts. eg. on Fedora: `dnf install siege`
* Java 11/17
* [Apache maven 3.5.3+](https://maven.apache.org/)
* the [oc](https://docs.openshift.com/container-platform/4.2/cli_reference/openshift_cli/getting-started-cli.html) 
or [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/) cli tools. (they can be used interchangeably in this demo) 
* An Openshift 4.2+ cluster (it may work on other Kubernetes clusters as well?)
* (OPTIONAL) [GraalVM CE](https://www.graalvm.org/) or [Mandrel](https://github.com/graalvm/mandrel)

### Local development

You can run the sample application with the following maven command.  
This will start a local runtime with virtually instantaneous hot reload of the application while you're making changes. 

Compile and run the app in dev mode: `mvn compile quarkus:dev`

And see your changes on the fly with eg. `curl localhost:8080` or `./localpoller.sh`

`ctrl-c ` when you're done

### Build project

* `mvn clean package -Pnative`, or if you don't have GraalVM/Mandrel installed locally, you can build it in a container (make sure you have podman or docker installed): `./buildNativeLinux.sh` | `./buildNativeMac.sh`

### Create Container Image
 
`docker build -f kubefiles/Dockerfile -t dev.local/kevindubois/quarked:1.0.0 .`

### Optionally Push Image to Quay (update with your repository name)

#docker login quay.io
#
#docker tag "$1" quay.io/kevindubois/quarked:1.0.0
#
#docker push quay.io/kevindubois/quarked:1.0.0

podman push "$1" docker://quay.io/kevindubois/quarked:1.0.0

### Install the Openshift Serverless Operator

Go to the operatorhub in your Openshift cluster, and find the Openshift Serverless Operator, and install it. 
Wait for the install to finish (the Operator will also install Service Mesh since it has a dependency on it), then apply the following file:
`kubectl apply -f kubefiles/serving.yml -f kubefiles/ConfigMap.yml` which will configure a knative instance.  

### Deploy to Openshift
Create a project if you haven't already, and then 
Deploy Knative service: `kubectl apply -f kubefiles/knService_quay.yml`

You can optionally also do a regular deployment (non-knative) and compare with the Knative service:
 `kubectl apply -f kubefiles/Deployment_quay.yml`
 You'll also have to expose the service in this case: `kubectl apply -f kubefiles/Service.yml`

### Start the siege!

You can either just do one request at a time with the `./knpoller.sh` (boring)

Or you can launch a bunch of concurrent requests and really see the pods scale: `./knburst.sh`

If you want to compare with the non-knative deployment, use poller.sh and burst.sh

### Troubleshooting
If you're seeing requests bounce or error out, your nodes may not be able to handle the pressure from the siege command.  
Try adjusting the siege parameters in the knburst.sh file; or configure knative's concurrency differently.

### Health / readiness / liveness checks
If you examined the pom.xml, you may have noticed the smallrye-health dependency.  Thanks to this, we have access to an automatic health api (check out the /q and /q/health endpoints)




