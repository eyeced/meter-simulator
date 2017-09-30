name := """meter-simulator"""
organization := "org.learn"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies += filters
libraryDependencies += "com.typesafe.akka"           %% "akka-persistence"    % "2.4.18"
libraryDependencies += "org.iq80.leveldb"             % "leveldb"             % "0.7"
libraryDependencies += "org.fusesource.leveldbjni"    % "leveldbjni-all"      % "1.8"
libraryDependencies += "org.apache.kafka"             % "kafka-clients"       % "0.10.2.1"
libraryDependencies += "com.fasterxml.jackson.core"   % "jackson-databind"    % "2.8.4"
libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.4"
libraryDependencies += "org.scalatestplus.play"      %% "scalatestplus-play"  % "2.0.0" % Test

libraryDependencies ++= Seq(
  ws,
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.16"
)

parallelExecution in Test := false

fork := true

import AssemblyKeys._

assemblySettings

mainClass in assembly := Some("play.core.server.ProdServerStart")
fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)

mergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "org.learn.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "org.learn.binders._"

