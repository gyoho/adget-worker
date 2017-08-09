name := "adget"

version := "1.0"

scalaVersion := "2.12.3"

val jacksonVersion = "2.9.0"

val jacksonAnnotations = "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion
val jacksonCore = "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion
val jacksonDatabind = "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
val jacksonDatatypeJoda = "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % jacksonVersion
val jacksonModuleScala = "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion
val jacksonJaxrsBase = "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-base" % jacksonVersion
val jacksonJaxrsJsonProvider = "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % jacksonVersion

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.typesafe.akka" %% "akka-actor" % "2.5.3",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.3" % Test,
  "com.twitter" %% "finagle-http" % "6.44.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "com.github.hipjim" % "scala-retry_2.12" % "0.2.2"
)
