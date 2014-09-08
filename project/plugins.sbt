resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
  url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
    Resolver.ivyStylePatterns)

resolvers += "JAnalyse Repository" at "http://www.janalyse.fr/repository/"

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.1.2")