package sparkec2

import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.scalatest.mock.MockitoSugar

class ImplSpec extends WordSpec with MockitoSugar with Matchers {
  "SparkConfig" should {

    "fail" in {
      intercept[RuntimeException] {
        SparkConfig.clusterConfig
      }
    }

  }

}

