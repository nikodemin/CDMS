import sbt._

object BuildConfig {

  object versions {
    val circe = "0.14.0-M1"

    val tapir = "0.17.0-M2"

    val scalactic = "3.2.0"
    val scalatest = "3.2.0"
    val scalacheck = "1.14.3"
    val `scalacheck-1-14` = "3.2.0.0"
    val scalamock = "5.0.0"

    val slf4j = "1.7.30"
    val logback = "1.2.3"

    val `scalacheck-shapeless_1.14` = "1.2.3"

    val akka = "2.6.10"
    val akkaPersistenceCassandra = "1.0.4"
    val akkaSprayJson = "10.2.0"
    val akkaProjection = "1.0.0"
    val `commons-io` = "2.4"
  }

  val testDependencies = Seq(
    "org.scalactic" %% "scalactic" % versions.scalactic,
    "org.scalatest" %% "scalatest" % versions.scalatest,
    "org.scalacheck" %% "scalacheck" % versions.scalacheck,
    "org.scalatestplus" %% "scalacheck-1-14" % versions.`scalacheck-1-14`,
    "org.scalamock" %% "scalamock" % versions.scalamock,
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % versions.`scalacheck-shapeless_1.14`,
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % versions.akka,
    "com.typesafe.akka" %% "akka-persistence-testkit" % versions.akka,
    "com.typesafe.akka" %% "akka-stream-testkit" % versions.akka,
    "commons-io" % "commons-io" % versions.`commons-io`
  ).map(_ % Test)

  val circeDependencies = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % versions.circe)

  val tapirDependencies = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core",
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe",
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs",
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml",
    "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server",
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http"
  ).map(_ % versions.tapir)

  val logDependencies = Seq(
    "org.slf4j" % "slf4j-api" % versions.slf4j,
    "ch.qos.logback" % "logback-classic" % versions.logback
  )

  val akkaDependencies = Seq(
    "com.typesafe.akka" %% "akka-persistence-typed" % versions.akka,
    "com.typesafe.akka" %% "akka-persistence-query" % versions.akka,
    "com.typesafe.akka" %% "akka-serialization-jackson" % versions.akka,
    "com.typesafe.akka" %% "akka-persistence-cassandra" % versions.akkaPersistenceCassandra,
    "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % versions.akkaPersistenceCassandra,
    "com.lightbend.akka" %% "akka-projection-cassandra" % versions.akkaProjection,
    "com.lightbend.akka" %% "akka-projection-eventsourced" % versions.akkaProjection,
    "com.typesafe.akka" %% "akka-http-spray-json" % versions.akkaSprayJson
  )

  val projectDependencies: Seq[ModuleID] = testDependencies ++ circeDependencies ++
    logDependencies ++ tapirDependencies ++ akkaDependencies

}
