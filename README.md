# Kubernetes HTTP example
This example is best tried out on Amazon Web Services (AWS). Use the ./cluster/kube-up.sh script from [k8s.io](https://github.com/GoogleCloudPlatform/kubernetes/blob/master/docs/getting-started-guides/aws.md) to start your cluster.

This example will do the following:

- Run a postgres database
- Run a service for postgres
- Run a webservice that connects to the database
- Run a load balancer of AWS

## KUBERNETES

```
kubectl create -f kubernetes/postgres-local-rc.json
kubectl create -f kubernetes/postgres-service.json
kubectl create -f kubernetes/http-rc.json

kubectl expose rc kubernetes-http-example --port=80 --target-port=http --type=LoadBalancer

kubectl describe service kubernetes-http-example
kubectl get pods -o wide
```

Wait a few minutes until the load balancer of AWS is ready. Then do some requests to the domain that ```kubectl describe service kubernetes-http-example``` returned. It will count up for every request.

Now terminate the machine which has postgres running on it. Kubernetes will automatically restart it on another machine. But you will notice, that the counter will restart. The reason is that the data is lost on the terminated server.

A way to overcome this problem is to use a EBS volume to store the database data.

### Switch to EBS volume
Create a small EBS volume in the same region as your kubernetes cluster.

```
aws ec2 create-volume --availability-zone eu-west-2a --size 5 --volume-type gp2
```

Get the volume id from created volume and add into next command.

```
export EBS_VOLUME=vol-9ce51589
sed -i '' "s/vol-[a-z0-9]*/$EBS_VOLUME/g" kubernetes/postgres-ebs-init.json
sed -i '' "s/vol-[a-z0-9]*/$EBS_VOLUME/g" kubernetes/postgres-ebs-rc.json
```

Now update your cluster to run postgres with this volume. Because we need some permissions on the data folder in postgres, we have a small init script that needs to run before starting postgres.

```
kubectl delete -f kubernetes/postgres-local-rc.json
kubectl create -f kubernetes/postgres-local-init.json
# wait until running!!
kubectl delete -f kubernetes/postgres-local-init.json
kubectl create -f kubernetes/postgres-ebs-rc.json
```

## Development
Create the docker image
```
cd http-server/
./gradlew createDockerImage
```

Run docker image
```
docker run -p 8000:8000\
 -e POSTGRES_HOST=192.168.59.103\
 -e POSTGRES_PORT=5432\
 -e POSTGRES_DATABASE=test\
 -e POSTGRES_USERNAME=postgres\
 -e POSTGRES_PASSWORD=test\
 --name http\
 pboos/kubernetes-http-example
```

Test if it is working
```
curl 192.168.59.103:8000
```