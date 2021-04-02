import caliban.tools.Codegen.GenType
import caliban.tools.{Codegen, Options}
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.dockerExposedPorts
import sbt.Keys.version

Compile / sourceGenerators += Def.task {
  import zio.Runtime

  val schemaPath = "conf/api/graphql/schema.graphql"
  val toPath = "target/generated-sources/graphql/src/main/scala/com/github/nikodemin/cdms/api/GraphQlApi.scala"
  val packageName = "com.github.nikodemin.cdms.api"
  val scalarMappings = "ID:java.util.UUID,OffsetDateTime:java.time.OffsetDateTime"

  val file = new File(toPath)
  file.getParentFile.mkdirs()

  val runtime = Runtime.default
  runtime.unsafeRun {
    Codegen.generate(Options.fromArgs(List(schemaPath, toPath, "--packageName", packageName, "--scalarMappings", scalarMappings)).get, GenType.Schema)
      .catchAll(reason => zio.console.putStrLn(reason.toString) *> zio.console.putStrLn(reason.getStackTrace.mkString("\n")))
      .as(1)
  }
  Seq((Compile / sourceManaged).value / toPath)
}.taskValue

lazy val coreProject = (project in file("."))
  .enablePlugins(JavaAppPackaging, DockerPlugin, CodegenPlugin)
  .settings(
    scalaVersion := "2.13.1",
    name := "CDMS",
    version := "0.1",
    resolvers ++= Seq(
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    ),
    libraryDependencies ++= BuildConfig.projectDependencies,
    scalacOptions += "-Ymacro-annotations",
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    dockerBaseImage := "adoptopenjdk/openjdk15:alpine",
    dockerExposedPorts += 9002
  )
  .dependsOn(validationProject)

lazy val validationProject = (project in file("validation"))
  .settings(
    scalaVersion := "2.13.1",
    libraryDependencies ++= BuildConfig.monocleDependencies
  )