resolvers += Resolver.url(
  "bintray Repository",
  url("http://dl.bintray.com/felixgborrego/repo"))(
    Resolver.ivyStylePatterns)

resolvers += "JAnalyse Repository" at "http://www.janalyse.fr/repository/"


addSbtPlugin("com.gilt" % "sbt-spark-ec2-plugin" % "0.1.14")