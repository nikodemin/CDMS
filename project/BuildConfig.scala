import sbt._

object BuildConfig {

  object versions {
    val monocle = "2.0.3"

    val scalatest = "3.3.0-SNAP3"
    val scalacheck = "3.3.0.0-SNAP2"
    val scalamock = "5.1.0"
    val `scalacheck-magnolia` = "0.5.1"
    val `scalacheck-shapeless_1.14` = "1.2.3"

    val slf4j = "1.7.30"
    val logback = "1.2.3"

    val `neo4j-driver` = "4.2.1"

    val zio = "1.0.4"
    val `zio-logging` = "0.5.6"
    val `zio-config` = "1.0.0-RC32"

    val caliban = "0.9.5"
  }

  val testDependencies = Seq(
    "org.scalatest" %% "scalatest" % versions.scalatest,
    "org.scalatestplus" %% "scalacheck-1-14" % versions.scalacheck,
    "org.scalamock" %% "scalamock" % versions.scalamock,
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % versions.`scalacheck-shapeless_1.14`,
    "com.github.chocpanda" %% "scalacheck-magnolia" % versions.`scalacheck-magnolia`,
    "dev.zio" %% "zio-test" % versions.zio,
    "dev.zio" %% "zio-test-sbt" % versions.zio,
    "dev.zio" %% "zio-test-magnolia" % versions.zio
  ).map(_ % Test)

  val logDependencies = Seq(
    "org.slf4j" % "slf4j-api" % versions.slf4j,
    "ch.qos.logback" % "logback-classic" % versions.logback
  )

  val zioDependencies = Seq(
    "dev.zio" %% "zio-macros" % versions.zio,
    "dev.zio" %% "zio" % versions.zio,
    "dev.zio" %% "zio-config" % versions.`zio-config`,
    "dev.zio" %% "zio-config-typesafe" % versions.`zio-config`,
    "dev.zio" %% "zio-logging" % versions.`zio-logging`,
    "dev.zio" %% "zio-logging-slf4j" % versions.`zio-logging`
  )

  val monocleDependencies = Seq(
    "com.github.julien-truffaut" %% "monocle-core",
    "com.github.julien-truffaut" %% "monocle-macro"
  ).map(_ % versions.monocle)

  val neo4jDependencies = Seq(
    "org.neo4j.driver" % "neo4j-java-driver" % versions.`neo4j-driver`
  )

  val calibanDependencies = Seq(
    "com.github.ghostdogpr" %% "caliban",
    "com.github.ghostdogpr" %% "caliban-http4s"
  ).map(_ % versions.caliban)

  val projectDependencies: Seq[ModuleID] = testDependencies ++ logDependencies ++ monocleDependencies ++
    zioDependencies ++ neo4jDependencies ++ calibanDependencies
}
