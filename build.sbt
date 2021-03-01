import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.dockerExposedPorts
import sbt.Keys.version

lazy val coreProject = (project in file("."))
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    scalaVersion := "2.13.1",
    name := "CDMS",
    version := "0.1",
    libraryDependencies ++= BuildConfig.projectDependencies,
    scalacOptions += "-Ymacro-annotations",
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    dockerBaseImage := "adoptopenjdk/openjdk15:alpine",
    dockerExposedPorts += 9002,
    PB.targets in Compile := Seq(
      scalapb.gen(grpc = true) -> (sourceManaged in Compile).value / "scalapb",
      scalapb.zio_grpc.ZioCodeGenerator -> (sourceManaged in Compile).value / "scalapb"
    )
  )
  .dependsOn(validationProject)

lazy val validationProject = (project in file("validation"))
  .settings(
    scalaVersion := "2.13.1",
    libraryDependencies ++= BuildConfig.monocleDependencies
  )