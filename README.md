sbt-spark-ec2-plugin
====================

Sbt plugin to submit Spark jobs to a remote EC2 Spark Cluster.
It allows you to create a cluster on demand and submit your task to a remote EC2 cluster from your local machine.
Unlike spark-submit, it allows you to deploy to a remote cluster. To do so, It copies the jar to the master node and 
execute spark-submit inside the master node.

![](https://raw2.github.com/felixgborrego/sbt-spark-ec2-plugin/master/docs/diagram.png)


[![Build Status](https://travis-ci.org/felixgborrego/lib-spark-manager.svg?branch=master)](https://travis-ci.org/felixgborrego/lib-spark-manager)

## Usage

This will create an assemlby jar and submit the jar to the Ec2 Spark cluster (setting up the cluster if it doesn't exist)

```scala
sbt sparkLaunchCluster
```

![](https://raw2.github.com/felixgborrego/sbt-spark-ec2-plugin/master/docs/sbt-spark-ec2.gif)


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
#stop the cluster after finish the job, by default false
autoStop=true
```




##How To Use

For sbt 0.11/0.12/0.13, add sbt-spark-ec2-plugin as a dependency in project/plugins.sbt:

```scala
resolvers += Resolver.url(
  "bintray Repository",
  url("http://dl.bintray.com/felixgborrego/repo"))(
    Resolver.ivyStylePatterns)

addSbtPlugin("com.gilt" % "sbt-spark-ec2-plugin" % "0.1.5")
```

Then, add the following to your <project-root>/build.sbt (that's not project/build.sbt!) as a standalone line:

```scala
sparkec2.Ec2SparkPluginSettings.sparkSettings
```

To prevent having a too fat jar and avoid conflicts you can define a merge strategy as follow:

```scala
libraryDependencies ++= Seq(
  ("org.apache.spark" %% "spark-core" % "1.1.0" % "provided" ).excludeAll(excludeMortbayJetty),
  "org.apache.spark" %% "spark-streaming" % "1.1.0" % "provided",
  "org.apache.spark" %% "spark-sql" % "1.1.0"  % "provided",
  "org.apache.spark" %% "spark-hive" % "1.1.0"  % "provided",
  "org.apache.spark" %% "spark-mllib" % "1.1.0"  % "provided"
)

mergeStrategy in assembly <<= (mergeStrategy in assembly) {
  (old) => {
    case PathList("com","hadoop", xs @ _*) => MergeStrategy.discard
    case PathList("org","hsqldb", xs @ _*) => MergeStrategy.discard
    case PathList("org","jboss", xs @ _*) => MergeStrategy.discard
    case PathList("org","mortbay", xs @ _*) => MergeStrategy.discard
    case PathList("org","objectweb", xs @ _*) => MergeStrategy.discard
    case PathList("org","objenesis", xs @ _*) => MergeStrategy.discard
    case PathList("org","slf4j", xs @ _*) => MergeStrategy.discard
    case PathList("org","znerd", xs @ _*) => MergeStrategy.discard
    case PathList("thrift", xs @ _*) => MergeStrategy.discard
    case PathList("junit", xs @ _*) => MergeStrategy.discard
    case PathList("org", "apache","jasper", xs @ _*) => MergeStrategy.first
    case PathList("org", "apache","commons", xs @ _*) => MergeStrategy.first
    case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.first
    case PathList("javax", "servlet", xs @ _*) => MergeStrategy.discard
    case "application.conf" => MergeStrategy.concat
    case "unwanted.txt"     => MergeStrategy.discard
    // case "project.clj" => MergeStrategy.discard // Leiningen build files
    case x if x.startsWith("META-INF") => MergeStrategy.discard // Bumf
    case x if x.endsWith(".html") => MergeStrategy.discard // More bumf
    case PathList("com", "esotericsoftware", xs@_*) => MergeStrategy.last // For Log$Logger.class
    case x => MergeStrategy.first
  }
}
```

## Compile 

sbt publish-local

## Roadmap

this project is still in an early stage, there are many things that should be improved:

- [X] Get cluster status
- [X] Stop cluster
- [ ] Schedule task and group task





