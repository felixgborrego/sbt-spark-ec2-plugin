import org.apache.spark.SparkContext

import scala.Array

/**
 * Create a local Spark Context and execute a simple task.
 */
object SimpleApp extends App {
  val testMode = args.contains("test")

  val sc = SparkUtil.sparkContext(testMode)

  performOperation(sc)

  sc.stop()

  /**
   * Example of a simple operation using the SparkContext.
   */
  def performOperation(spark: SparkContext) {
    val slices = 2
    val n = 100000 * slices
    val count = spark.parallelize(1 to n, slices).map { i =>
      val x = Math.random * 2 - 1
      val y = Math.random * 2 - 1
      if (x * x + y * y < 1) 1 else 0
    }.reduce(_ + _)
    println("\n\n\n\n\nPi is roughly " + 4.0 * count / n)
  }
}