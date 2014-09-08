import bintray.AttrMap
import bintray._


sbtPlugin := true

name := "sbt-spark-ec2-plugin"

organization := "com.gilt"

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.2")


libraryDependencies ++= Seq(
  "org.mockito"             %  "mockito-all"              % "1.9.5"          % "test",
  "org.specs2"              %% "specs2"                   % "2.4.1"          % "test",
  "org.scalatest"           %% "scalatest"                % "2.1.7"          % "test"
)


resolvers += Resolver.url(
  "bintray Repository",
  url("http://dl.bintray.com/felixgborrego/repo"))(
    Resolver.ivyStylePatterns)


libraryDependencies += "com.gilt" %% "lib-spark-manager" % "0.0.3.9"

publishMavenStyle := false

bintrayPublishSettings

bintray.Keys.repository in bintray.Keys.bintray := "repo"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

bintray.Keys.bintrayOrganization in bintray.Keys.bintray := None
