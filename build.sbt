sbtPlugin := true

name := "sbt-spark-ec2-plugin"

organization := "com.gilt"

libraryDependencies += "com.gilt" %% "lib-spark-manager" % "0.0.2"

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.2")


libraryDependencies ++= Seq(
  "org.mockito"             %  "mockito-all"              % "1.9.5"          % "test",
  "org.specs2"              %% "specs2"                   % "2.4.1"          % "test",
  "org.scalatest"           %% "scalatest"                % "2.1.7"          % "test"
)