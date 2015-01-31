package sparkec2

import java.io.File

import com.amazonaws.auth.{ AWSCredentials, BasicAWSCredentials }
import com.amazonaws.regions.{ Region, Regions }
import com.fgb.spark
import com.fgb.spark._
import com.typesafe.config.ConfigFactory
import sbt.{ Logger, _ }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }
import scala.util.Try

private object Implementation {

  def sparkStart(awsCredentials: AWSCredentials, region: String, clusterConfig: ClusterConf, log: Logger): Unit = {
    val manager = SparkCluster(awsCredentials, Region.getRegion(Regions.fromName(region)), new SbtLogger(log))

    val result = for {
      cluster <- manager.start(clusterConfig)
      status <- manager.status(cluster)
    } yield status

    printInfo(result.await)
  }

  def stop(awsCredentials: AWSCredentials, region: String, clusterConfig: ClusterConf, force: Boolean, log: Logger): Unit = {
    val manager = SparkCluster(awsCredentials, Region.getRegion(Regions.fromName(region)), new SbtLogger(log))
    manager.stop(clusterConfig.name, force).await
  }

  def sparkSubmit(awsCredentials: AWSCredentials, region: String, clusterConfig: ClusterConf, mainClass: String, jar: File, log: Logger): Unit = {
    val manager = SparkCluster(awsCredentials, Region.getRegion(Regions.fromName(region)), new SbtLogger(log))
    val result = for {
      cluster <- manager.start(clusterConfig)
      job <- manager.execute(JobConf(mainClass, jar, cluster))
      status <- manager.status(cluster)
      jobStatus <- manager.status(job)
    } yield (status, jobStatus)

    val (clusterStatus, jobStatus) = result.await
    printInfo(clusterStatus, Some(jobStatus))
  }

  def sparkInfo(awsCredentials: AWSCredentials, region: String, clusterConfig: ClusterConf, log: Logger): Unit = {
    val manager = SparkCluster(awsCredentials, Region.getRegion(Regions.fromName(region)), new SbtLogger(log))

    val result = for {
      cluster <- manager.find(clusterConfig.name)
      status <- manager.status(cluster)
    } yield status

    printInfo(result.await)
  }

  def printInfo(status: ClusterStatus, job: Option[JobStatus] = None): Unit = {
    val cluster = status.cluster
    val dns = status.cluster.master.publicDNSName

    val stepInfo = job.orElse(status.jobs.lastOption).map(step =>
      s"""--------------------------------------
           |Step Id:          ${step.id}
           |Step name:        ${step.name}
           |Created:          ${step.creation.getOrElse("")} -> ${step.end.getOrElse("")}
           |Step status:      ${step.state} [${step.message}]
           |Step logs:        http://${dns}:9101/logs/steps/${step.id}/stderr
           |""".stripMargin).getOrElse("")

    val info = s"""|--------------------------------------
         |Cluster Id:       ${cluster.id}, Cluster name: ${cluster.name}, Status:${status.state}
         |
         |PublicIpAddress:  ${cluster.master.publicIpAddress}
         |PublicDnsName:    ${dns}
         |Connect with ssh: ssh hadoop@${dns} -i ~/SparkEc2KeyPair.pem
         |Spark UI:         http://${dns}:8080
         |HDFS Web UI:      http://${dns}:9101
         |Ganglia monitor   http://${dns}/ganglia
         |
         |Logs:             /mnt/var/log/hadoop
         |                  http://${dns}:9101/logs
         |
         |$stepInfo
         |--------------------------------------
         |Configure SSH tunnel: ssh -ND 8157 hadoop@${dns} -i ~/SparkEc2KeyPair.pem
         |Info to connect to the cluster: http://docs.aws.amazon.com/ElasticMapReduce/latest/DeveloperGuide/emr-connect-master-node-proxy.html
         |--------------------------------------
         |""".stripMargin

    println(info)
  }

  implicit class AwaitableFuture[A](val f: Future[A]) extends AnyVal {
    @inline def await: A = Await.result(f, Duration.Inf)
  }
}

/**
 * Default Values.
 * If the build.sbt file doesn't include the required parameters
 * the plugin will use by default the conf in /spark.conf
 */
private object SparkDefaultConfig {
  import com.fgb.spark.impl.util._
  private lazy val conf = ConfigFactory.parseFile(new File("spark.conf"))

  def clusterConfig = Try(ClusterConf(conf)).recover{
    case e => sys.error(s"File spark.conf required with a valid cluster configuration. $e")
  }.get

  def accessKey = conf.getOptionalString("cluster.ec2.credentials.key")
  def secretKey = conf.getOptionalString("cluster.ec2.credentials.secret")

  def awsCredentialsDefault: AWSCredentials = (accessKey, secretKey) match {
    case (Some(key), Some(secret)) =>
      new BasicAWSCredentials(key, secret)
    case _ => sys.error(s"Required AWS credentials missed. Add credentials required 'cluster.ec2.credentials.key' and 'cluster.ec2.credentials.secret' to spark.conf")
  }

  def regionDefault = conf.getOptionalString("cluster.ec2.region").getOrElse(sys.error(s"Required AWS region missed. Define region in build.sbt or add required 'cluster.ec2.region' to spark.conf"))

  def mainJobClassDefault: String = conf.getOptionalString("cluster.job.main-class").getOrElse(sys.error(s"Main job class missed. Define 'cluster.job.main-class' in spark.conf"))
}

private class SbtLogger(sbtLogger: Logger) extends spark.Logger {
  override def logError(msg: => String): Unit = sbtLogger.log(Level.Error, msg)

  override def logDebug(msg: => String): Unit = sbtLogger.log(Level.Debug, msg)

  override def logInfo(msg: => String): Unit = sbtLogger.log(Level.Info, msg)

  override def logWarn(msg: => String): Unit = sbtLogger.log(Level.Warn, msg)
}