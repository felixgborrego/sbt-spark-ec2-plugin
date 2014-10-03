
import utils.SparkUtils._

/**
 * Create a remote Spark Context and execute a simple task.
 */
object SimpleRemoteApp {

  def main(args: Array[String]) {

    val sc = remoteSparkContext

    SimpleApp.performOperation(sc)

  }
}