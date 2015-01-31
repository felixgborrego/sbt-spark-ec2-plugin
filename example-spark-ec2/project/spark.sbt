resolvers += Resolver.url(
  "bintray Repository",
  url("http://dl.bintray.com/felixgborrego/repo"))(
    Resolver.ivyStylePatterns)

resolvers += "JAnalyse Repository" at "http://www.janalyse.fr/repository/"

addSbtPlugin("com.fgb" % "sbt-spark-ec2-plugin" % "0.3.0.18")
