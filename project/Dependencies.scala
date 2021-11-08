import sbt._

object Dependencies {

  object versions {
    val cats = "3.2.9"
    val enumeratum = "1.7.0"
    val chimney = "0.6.1"
    val monocle = "3.0.0-M6"

    val scalatest = "3.2.10"
    val scalamock = "5.1.0"
    val testcontainersScala = "0.39.11"
    val `cats-effect-testing-scalatest` = "1.3.0"

    val slf4j = "1.7.32"
    val logback = "1.2.6"

    val `neo4j-gremlin-bolt` = "0.4.6"
    val `gremlin-scala` = "3.4.7.17"

    val `tinkergraph-gremlin` = "3.5.1"

    val caliban = "0.9.5+323-eadefd48+20211106-0644-SNAPSHOT"
  }

  val enumeratum = Seq("com.beachape" %% "enumeratum" % versions.enumeratum)

  val chimney = Seq("io.scalaland" %% "chimney" % versions.chimney)

  val test = Seq(
    "org.scalatest" %% "scalatest" % versions.scalatest,
    "org.scalamock" %% "scalamock" % versions.scalamock,
    "com.dimafeng" %% "testcontainers-scala-scalatest" % versions.testcontainersScala,
    "com.dimafeng" %% "testcontainers-scala-neo4j" % versions.testcontainersScala,
    "org.typelevel" %% "cats-effect-testing-scalatest" % versions.`cats-effect-testing-scalatest`
  ).map(_ % Test)

  val cats = Seq("cats-effect").map("org.typelevel" %% _ % versions.cats)

  val log = Seq(
    "org.slf4j" % "slf4j-api" % versions.slf4j,
    "ch.qos.logback" % "logback-classic" % versions.logback
  )

  val monocle = Seq("monocle-core", "monocle-macro").map("com.github.julien-truffaut" %% _ % versions.monocle)

  val db = Seq(
    "com.michaelpollmeier" %% "gremlin-scala" % versions.`gremlin-scala`,
    "com.steelbridgelabs.oss" % "neo4j-gremlin-bolt" % versions.`neo4j-gremlin-bolt`
  )

  val caliban = Seq("caliban", "caliban-http4s", "caliban-cats").map("com.github.ghostdogpr" %% _ % versions.caliban)

  val live: Seq[ModuleID] = test ++ log ++ monocle ++ cats ++ db ++ caliban ++ enumeratum ++ chimney
}
