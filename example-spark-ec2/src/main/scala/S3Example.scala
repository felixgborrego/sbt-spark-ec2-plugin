
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD

/**
 * Example to show how to process a txt file on S3.
 * This will take a file with a street name per line and return the more used street name.
 * The file contain all the street names in the world.
 */
object S3Example extends App {
  val testMode = args.contains("test")

  val sc = SparkUtil.sparkContext(testMode)

  val file = "/Users/fborrego/Dropbox/streets.csv"
  val rdd = sc.textFile(file)

  performOperation(sc, rdd)

  sc.stop()

  /**
   * Example of a simple operation using the SparkContext.
   */
  def performOperation(spark: SparkContext, inputRdd: RDD[String]) {

    val rdd = if (testMode) {
      println("Using a partial RDD with 1% of the data for testing")
      inputRdd.randomSplit(Array(0.01, 0.99)).apply(0)
    } else {
      inputRdd
    }

    println(s"Count: ${rdd.count}")

    // get the more common street name in earth!
    val streetsByNumber = rdd.map(name => (name, 1)).reduceByKey(_ + _).sortBy(_._2, false)
    streetsByNumber.take(30).foreach(println)

  }
}