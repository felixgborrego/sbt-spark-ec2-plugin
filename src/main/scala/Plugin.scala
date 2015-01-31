package sparkec2

import com.amazonaws.auth.AWSCredentials
import com.fgb.spark.ClusterConf
import sbt.Keys._
import sbt._

object SparkTaskKeys {
  lazy val sparkStart = taskKey[Unit]("Create a new cluster or return the info of an existing cluster if there is already a cluster with the given name")
  lazy val sparkStop = taskKey[Unit]("Stop the EMR cluster waiting until there is no more job running")
  lazy val sparkShutdown = taskKey[Unit]("Shutdown the cluster immediately")
  lazy val sparkSubmitJob = taskKey[Unit]("Submit the job to the cluster, adding a new step in the EMR cluster. It'll create a new cluster if if doesn't exist yet.")
  lazy val sparkInfo = taskKey[Unit]("Describe the status of the current cluster and jobs")
  lazy val sparkLogs = taskKey[Unit]("Show the last related logs in the cluster")

  lazy val clusterName = settingKey[String]("Cluster's name, It's used to group job into a single EMR cluster")
  lazy val s3Bucket = settingKey[String]("S3 bucket to use to store jar,jobs,scripts and logs")
  lazy val region = settingKey[String]("Region where the cluster will be deployed")
  lazy val mainJobClass = settingKey[String]("Main class in side the jar to execute")
  lazy val awsCredentials = settingKey[AWSCredentials]("Main class in side the jar to execute")
  lazy val clusterConfig = settingKey[ClusterConf]("Cluster description (num cores, instance types,...)")
  lazy val ec2KeyPairFile = settingKey[File]("Ec2 key pair file in pem format)")

}

object Ec2SparkPlugin extends AutoPlugin {
  override def projectSettings: Seq[Def.Setting[_]] = Ec2SparkPluginSettings.sparkSettings
}

object Ec2SparkPluginSettings {
  import Defaults._
  lazy val sparkSettings: Seq[Setting[_]] = Seq(regionKey, mainJobClassKey, awsCredentialsKey, clusterConfigKey, sparkStartKey, sparkStopKey, sparkShutdownKey, sparkSubmitJobKey, sparkInfoKey)
}

object Defaults {
  import SparkTaskKeys._

  def regionKey = region := SparkDefaultConfig.regionDefault
  def mainJobClassKey = mainJobClass := SparkDefaultConfig.mainJobClassDefault
  def awsCredentialsKey = awsCredentials := SparkDefaultConfig.awsCredentialsDefault
  def clusterConfigKey = clusterConfig := SparkDefaultConfig.clusterConfig

  def sparkStartKey = sparkStart := {
    val log = (streams in sparkStart).value.log
    Implementation.sparkStart(awsCredentials.value, region.value, clusterConfig.value, log)
  }

  def sparkStopKey = sparkStop := {
    val log = (streams in sparkStop).value.log
    Implementation.stop(awsCredentials.value, region.value, clusterConfig.value, false, log)
  }

  def sparkShutdownKey = sparkShutdown := {
    val log = (streams in sparkShutdown).value.log
    Implementation.stop(awsCredentials.value, region.value, clusterConfig.value, true, log)
  }

  def sparkSubmitJobKey = sparkSubmitJob := {
    val assemblyFile = sbtassembly.Plugin.AssemblyKeys.assembly.value
    val log = (streams in sparkSubmitJob).value.log
    Implementation.sparkSubmit(awsCredentials.value, region.value, clusterConfig.value, mainJobClass.value, assemblyFile, log)
  }

  def sparkInfoKey = sparkInfo := {
    val log = (streams in sparkInfo).value.log
    Implementation.sparkInfo(awsCredentials.value, region.value, clusterConfig.value, log)
  }

}

