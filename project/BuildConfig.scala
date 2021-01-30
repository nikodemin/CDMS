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

    val `grpc-netty` = "1.35.0"
  }

  val testDependencies = Seq(
    "org.scalatest" %% "scalatest" % versions.scalatest,
    "org.scalatestplus" %% "scalacheck-1-14" % versions.scalacheck,
    "org.scalamock" %% "scalamock" % versions.scalamock,
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % versions.`scalacheck-shapeless_1.14`,
    "com.github.chocpanda" %% "scalacheck-magnolia" % versions.`scalacheck-magnolia`,
  ).map(_ % Test)

  val logDependencies = Seq(
    "org.slf4j" % "slf4j-api" % versions.slf4j,
    "ch.qos.logback" % "logback-classic" % versions.logback
  )

  val zioDependencies = Seq(
    "io.grpc" % "grpc-netty" % versions.`grpc-netty`,
    "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion
  )

  val monocleDependencies = Seq(
    "com.github.julien-truffaut" %% "monocle-core",
    "com.github.julien-truffaut" %% "monocle-macro"
  ).map(_ % versions.monocle)

  val projectDependencies: Seq[ModuleID] = testDependencies ++ logDependencies ++ zioDependencies ++ monocleDependencies
}
