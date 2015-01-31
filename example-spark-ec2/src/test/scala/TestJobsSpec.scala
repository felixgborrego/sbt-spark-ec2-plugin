
import org.specs2.mutable.Specification

/**
 * Test in local with an small set of data before submit the job to the real cluster!.
 */
class TestJobsSpec extends Specification {
  // Only one SparkContext each time

  sequential

  "SimpleApp" should {

    "execute an Spark job in local Mode" in {
      SimpleApp.main(Array("test"))
      success("Done!")
    }
  }

  "S3Example" should {

    "execute an Spark job in local mode" in {
      S3Example.main(Array("test"))
      success("Done!")
    }
  }

}
