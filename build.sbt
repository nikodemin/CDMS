import caliban.tools.{Codegen, Options, SchemaWriter}
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.dockerExposedPorts
import sbt.Keys.version

lazy val genApiTask = TaskKey[Unit]("genApi", "Generates Scala API classes")

genApiTask := {
  import zio.Runtime

  val runtime = Runtime.default
  runtime.unsafeRun(Codegen.generate(Options.fromArgs(
    List("conf/api/graphql/schema.graphql", "target/GraphQlApi.scala")).get,
    (schema, objectName, packageName, _, effect) => SchemaWriter.write(schema, objectName, packageName, effect)))
}

(compile in Compile) := ((compile in Compile) dependsOn genApiTask).value

lazy val coreProject = (project in file("."))
  .enablePlugins(JavaAppPackaging, DockerPlugin, CodegenPlugin)
  .settings(
    scalaVersion := "2.13.1",
    name := "CDMS",
    version := "0.1",
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