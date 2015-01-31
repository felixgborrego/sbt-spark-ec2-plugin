import org.apache.spark.{ SparkContext, SparkConf }

object SparkUtil {
  def sparkContext(testMode: Boolean) = {
    val conf = new SparkConf().setAppName("test")
    if (testMode) {
      conf.setMaster("local[4]") // local execution
    }

    new SparkContext(conf)

  }
}
