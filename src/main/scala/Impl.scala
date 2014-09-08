package sparkec2

import java.io.{ FileNotFoundException, FileReader, File }
import java.util.Properties
import com.gilt.spark.manager.SparkEc2Manager

import collection.JavaConversions._
import com.gilt.spark.manager.model.{ ClusterType, ClusterConfig, LocalConfig }
import sbt._

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }

private object Implementation {
  def sparkLaunchCluster(jar: File, localConfig: LocalConfig, clusterConfig: ClusterConfig, log: Logger) = {
    log.info(s"Deploying $jar to ${localConfig.localSparkHome} ")

    val ec2Manager = SparkEc2Manager()

    ec2Manager.getCluster(localConfig).await match {
      case None => log.log(Level.Info, s"Cluster doesn't exist. Creating cluster first")
      case Some(host) => log.log(Level.Info, s"Cluster already exist. $host")
    }

    val cluster = ec2Manager.createCluster(localConfig, clusterConfig).await

    log.log(Level.Info, s"Launching job ${localConfig.classJob} inside $jar")
    ec2Manager.executeJob(jar.getAbsolutePath, localConfig.classJob, cluster).await


    log.log(Level.Info, s"The job ${localConfig.classJob} is done!")
    if (clusterConfig.autoStop) {
      log.log(Level.Info, s"autoStop = true. Shut downing the cluster")
      ec2Manager.destroy(localConfig)
    } else {
      log.log(Level.Info, s"autoStop = false, The cluster is still running")
    }
  }

  implicit class AwaitableFuture[A](val f: Future[A]) extends AnyVal {
    @inline def await: A = Await.result(f, Duration.Inf)
  }
}

/**
 * Load data from spark.conf.
 */
private object SparkConfig {

  val properties = {
    val properties = new Properties()
    try {
      properties.load(new FileReader("spark.conf"))
    } catch {
      case _: FileNotFoundException =>
    }
    properties.toMap
  }

  val localSparkHome = properties.get("localSparkHome")
  val keyPair = properties.get("keyPair")
  val keyFile = properties.get("keyFile")
  val awsAccessKeyId = properties.get("awsAccessKeyId")
  val awsSecretKey = properties.get("awsSecretKey")
  val region = properties.get("region")
  val numMasters = properties.get("clusterNumMaster").map(_.toInt)
  val numWorkers = properties.get("clusterNumWorkers").map(_.toInt)
  val classJob = properties.get("classJob")
  val autoStop = properties.get("autoStop").map(_.toString.toBoolean).getOrElse(false)

  def localConfig = (classJob, localSparkHome, keyPair, keyFile, awsAccessKeyId, awsSecretKey, region) match {
    case (Some(classJob), Some(localSparkHome), Some(keyPair), Some(keyFile), Some(awsAccessKeyId), Some(awsSecretKey), Some(region)) =>
      LocalConfig(classJob, localSparkHome, keyPair, keyFile, awsAccessKeyId, awsSecretKey, region)
    case _ =>
      sys.error(s"Parameter are required. Define sparkLocalConfig in build.sbt or add required parameters to spark.conf")
  }

  def clusterConfig = (numMasters, numWorkers) match {
    case (Some(numMasters), Some(numWorkers)) =>
      ClusterConfig(new ClusterType(numMasters, numWorkers), autoStop)
    case _ =>
      sys.error(s"Parameter are required. Define sparkClusterConfig in build.sbt or add required parameters to spark.conf")
  }

}