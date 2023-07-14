name         := """arc-reactor-scala"""
organization := "com.github.ag"
version      := "1.0.0"

lazy val root = (project in file("."))

inThisBuild(
  List(
    scalaVersion := "3.3.0",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision
  )
)

libraryDependencies ++= Seq(
  "com.linecorp.armeria" % "armeria-logback" % "1.24.2",
  "ch.qos.logback" % "logback-classic" % "1.4.8",
  "org.neo4j.driver" % "neo4j-java-driver" % "5.9.0",
  "org.neo4j" % "neo4j-cypher-dsl" % "2023.3.2",
  "org.neo4j" % "neo4j" % "5.9.0",
  "org.neo4j" % "neo4j-ogm-core" % "4.0.5",
  "org.neo4j" % "neo4j-ogm-embedded-driver" % "3.2.40",
  "org.neo4j" % "neo4j-ogm-bolt-driver" % "4.0.5",
  "com.linecorp.armeria" %% "armeria-scala" % "1.20.2",
  "com.github.andyglow" %% "typesafe-config-scala" % "2.0.0",
  "org.yaml" % "snakeyaml" % "2.0",
)
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.13.5"

scalacOptions ++= Seq(
  "deprecation",
  "-feature",
  "utf8",
  "-Xfatal-warnings"
)

Global / onChangedBuildSource := ReloadOnSourceChanges

scalafixOnCompile := true

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard // Fix merge Strategy
  case x => MergeStrategy.first
}
