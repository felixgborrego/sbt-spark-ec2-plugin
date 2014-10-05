package utils

import org.apache.spark.{ SparkContext, SparkConf }

object SparkUtils {

  def localSparkContext = {
    val conf = new SparkConf().setAppName("Local Cluster")
    conf.setMaster("local[2]")
    new SparkContext(conf)
  }

  def remoteSparkContext = {
    val conf = new SparkConf().setAppName("Remote Cluster")
    new SparkContext(conf)
  }

}
