import sbt._

object BuildConfig {

  object versions {
    val `scalacheck-magnolia` = "0.5.1"

    val monocle = "2.0.3"

    val scalatest = "3.3.0-SNAP3"
    val scalacheck = "3.3.0.0-SNAP2"
    val scalamock = "5.1.0"

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
    "org.scalatest" %% "scalatest" % versions.scalatest,
    "org.scalatestplus" %% "scalacheck-1-14" % versions.scalacheck,
    "org.scalamock" %% "scalamock" % versions.scalamock,
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % versions.`scalacheck-shapeless_1.14`,
    "com.github.chocpanda" %% "scalacheck-magnolia" % versions.`scalacheck-magnolia`,
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % versions.akka,
    "com.typesafe.akka" %% "akka-persistence-testkit" % versions.akka,
    "com.typesafe.akka" %% "akka-stream-testkit" % versions.akka,
    "commons-io" % "commons-io" % versions.`commons-io`
  ).map(_ % Test)

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

  val monocleDependencies = Seq(
    "com.github.julien-truffaut" %% "monocle-core",
    "com.github.julien-truffaut" %% "monocle-macro"
  ).map(_ % versions.monocle)

  val projectDependencies: Seq[ModuleID] = testDependencies ++ logDependencies ++ akkaDependencies ++ monocleDependencies
}
