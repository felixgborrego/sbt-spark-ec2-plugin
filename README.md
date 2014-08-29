Work in progress...

## Description

Sbt plugin to submit Spark jobs to a remote EC2 Spark Cluster.
It allows you to create a cluster on demand and submit your task to a remote EC2 cluster from your computer.
Unlike spark-submit, it allows you to deploy to a remote cluster. To do so, It copies the jar to the master node and 
execute spark-submit inside the master node.


![](https://raw2.github.com/felixgborrego/sbt-spark-ec2-plugin/master/docs/diagram.png)

## Usage

This will create a slim jar (wihtout dependencies) and submit the jar to the Ec2 Spark cluster (setting up the cluster if it doesn't exist)

```scala
sbt submitSparkJob
```

This will create a fat jar and submit the jar to the Ec2 Spark cluster (setting up the cluster if it doesn't exist)

```scala
sbt submitAssemblySparkJob
```

To make the plugin know what if your configuration you have to create a file spark.conf in your project root. With the 
following content:

```scala
localSparkHome = /Users/.../spark-1.0.2-bin-hadoop1/
keyPair = aws key pair
keyFile = /Users/.../aws-key.pem
awsAccessKeyId = AWS KEY
awsSecretKey =  AWS Secret Key
region = eu-west-1
clusterNumMaster=1
clusterNumWorkers=1
#classJob=class name
```

## Compile 

sbt publish-local

## Roadmap

this project is still in an early stage, there are many things that should be improved:

- [ ] Get cluster status
- [ ] Stop cluster
- [ ] Schedule task and group task
- [ ] Allow different regions
- [ ] ...




