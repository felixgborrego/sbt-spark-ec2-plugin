import sbtassembly.Plugin.AssemblyKeys
import AssemblyKeys._
import sbtassembly.Plugin.MergeStrategy
import sbtassembly.Plugin._

net.virtualvoid.sbt.graph.Plugin.graphSettings
sparkec2.Ec2SparkPluginSettings.sparkSettings
assemblySettings


name := "examples-spark-ec2-emr"
organization := "com.fgb"
scalaVersion := "2.10.4"

val sparkVersion = "1.1.0"

val excludeAlreadyInSpark: Seq[sbt.ExclusionRule] = Seq(
  ExclusionRule(organization = "org.apache.httpcomponents"),
  ExclusionRule(organization = "org.mortbay.jetty", name="servlet-api" ),
  ExclusionRule(organization = "org.mortbay.jetty", name="servlet-api-2.5" ),
  ExclusionRule(organization = "org.eclipse.jetty.orbit"),
  ExclusionRule(organization = "org.json4s"),
  ExclusionRule(organization = "org.codehaus.jackson"),
  ExclusionRule(organization = "commons-codec"),
  ExclusionRule(organization = "commons-logging"),
  ExclusionRule(organization = "com.fasterxml.jackson.core")
)

assemblyOption in assembly ~= { _.copy(includeScala = false) }

// runtime dependencies
libraryDependencies ++= Seq(
  ("org.apache.spark" %% "spark-core" % sparkVersion % "provided" ),
  "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion  % "provided",
  "org.apache.spark" %% "spark-mllib" % sparkVersion  % "provided",
  ("org.apache.avro" % "avro"            % "1.7.6" % "provided").excludeAll(excludeAlreadyInSpark:_*) ,
  ("org.apache.avro" % "avro-mapred"     % "1.7.6" % "provided").excludeAll(excludeAlreadyInSpark:_*)
)



// testing dependencies
libraryDependencies ++= Seq(
  "org.mockito"              %  "mockito-all"                 % "1.9.5"          % "test",
  "org.specs2"              %% "specs2"                       % "2.4.3"          % "test",
  ("org.apache.hadoop"        % "hadoop-test"                  % "1.0.2"          % "test").excludeAll(excludeAlreadyInSpark:_*),
  ("com.sun.jersey" % "jersey-core" % "1.8" % "test").excludeAll(excludeAlreadyInSpark:_*) ,
  ("com.sun.jersey" % "jersey-server" % "1.8"  % "test").excludeAll(excludeAlreadyInSpark:_*)
)


mergeStrategy in assembly <<= (mergeStrategy in assembly) {
  (old) => {
    case x if x.startsWith("META-INF") => MergeStrategy.discard
    case _ => MergeStrategy.first
  }
}
