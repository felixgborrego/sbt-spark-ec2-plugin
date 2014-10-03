/* SimpleApp.scala */

import org.apache.spark.SparkContext

import scala.math.random
import utils.SparkUtils._

/**
 * Create a local Spark Context and execute a simple task.
 */
object SimpleApp {

  def main(args: Array[String]) {

    val sc = localSparkContext

    performOperation(sc)

    sc.stop()
  }

  /**
   * Example of a simple operation using the SparkContext.
   */
  def performOperation(spark: SparkContext) {
    val slices = 2
    val n = 100000 * slices
    val count = spark.parallelize(1 to n, slices).map { i =>
      val x = random * 2 - 1
      val y = random * 2 - 1
      if (x * x + y * y < 1) 1 else 0
    }.reduce(_ + _)
    println("\n\n\n\n\nPi is roughly " + 4.0 * count / n)
  }
}