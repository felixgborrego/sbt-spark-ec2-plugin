package sparkec2

import com.gilt.spark.manager.model.{ ClusterConfig, LocalConfig }
import sbt.Keys._
import sbt._

object Ec2SparkPlugin extends AutoPlugin {

  override def projectSettings: Seq[Def.Setting[_]] = Ec2SparkPluginSettings.sparkSettings
}

object Ec2SparkPluginSettings {
  import Defaults._
  lazy val sparkSettings: Seq[Setting[_]] = Seq(generateSparkLaunchClusterTask, generateSparkLocalConfigKey, generateSparkClusterConfigKey)
}

object SparkTaskKeys {
  lazy val sparkLaunchCluster = taskKey[Unit]("Launch the cluster if it doesn't exist")
  lazy val sparkLocalConfig = settingKey[LocalConfig]("Configuration to access to AWS and about your local Spark installation")
  lazy val sparkClusterConfig = settingKey[ClusterConfig]("Configuration of the cluster in EC2")
}

object Defaults {
  import SparkTaskKeys._

  def generateSparkLocalConfigKey = sparkLocalConfig := SparkConfig.localConfig

  def generateSparkClusterConfigKey = sparkClusterConfig := SparkConfig.clusterConfig

  def generateSparkLaunchClusterTask = sparkLaunchCluster := {
    val assemblyFile = sbtassembly.Plugin.AssemblyKeys.assembly.value
    val localConfig = sparkLocalConfig.value
    val clusterConfig = sparkClusterConfig.value
    val log = (streams in sparkLaunchCluster).value.log

    Implementation.sparkLaunchCluster(assemblyFile, localConfig, clusterConfig, log)
  }
}

