[![Build Status](https://travis-ci.org/felixgborrego/lib-spark-manager.svg?branch=master)](https://travis-ci.org/felixgborrego/lib-spark-manager)

## Description

Sbt plugin to submit Spark jobs to a Amazon Elastic Map Reduce/EC2 Spark Cluster.
It allows you to create a cluster on demand and submit your task to a remote EC2 cluster from your computer.
Unlike spark-submit, it allows you to deploy to a remote cluster. To do so, it setup the cluster using Amazon EMR, and submit the job as an
 custom EMR step.

 ![](https://raw2.github.com/felixgborrego/sbt-spark-ec2-plugin/master/docs/diagram.png)


## Usage

* **sparkStart** Create a new EMR cluster or return the info of an existing cluster if there is already a cluster with the same cluster name.
* **sparkStop** Stop the EMR cluster waiting until there is no more job running
* **sparkShutdown** Shutdown the cluster immediately
* **sparkSubmitJob** This will assembly a jar with the job and submit the job to EMR, adding a new step in the EMR cluster. It'll create a new cluster if if doesn't exist yet.
* **sparkInfo** Show info about the cluster and the last jobs (step)
* **sparkLogs** Show the last related logs in the cluster (EMR step logs, Spark job output, master output, workers output,...)


```scala
sbt sparkStart
```

## Configure plugin

You have to add the following properties to the spark.conf file in your project root
