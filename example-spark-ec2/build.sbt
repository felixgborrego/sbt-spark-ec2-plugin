import AssemblyKeys._
import sbtassembly.Plugin.MergeStrategy

sparkec2.Ec2SparkPluginSettings.sparkSettings

assemblySettings

name := "SimpleSparkProject"

version := "1.0"

scalaVersion := "2.10.4"

scalacOptions += "-target:jvm-1.7"

jarName in assembly := "sparkJob.jar"

val sparkVersion = "1.1.0"

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"


libraryDependencies ++= Seq(
  ("org.apache.spark" %% "spark-core" % sparkVersion % "provided" ),
  "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion  % "provided",
  "org.apache.spark" %% "spark-mllib" % sparkVersion  % "provided"
)

libraryDependencies ++= Seq(
  "org.mockito"             %  "mockito-all"              % "1.9.5"          % "test",
  "org.specs2"              %% "specs2"                   % "2.4.3"          % "test"
)



