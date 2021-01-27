import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.dockerExposedPorts
import sbt.Keys.version

lazy val coreProject = (project in file("."))
  .enablePlugins(JavaAppPackaging, DockerPlugin, AkkaGrpcPlugin)
  .settings(
    scalaVersion := "2.12.12",
    name := "CDMS",
    version := "0.1",
    libraryDependencies ++= BuildConfig.projectDependencies,
    dockerBaseImage := "adoptopenjdk/openjdk15:alpine",
    dockerExposedPorts += 9002
  )
  .dependsOn(validationProject)

lazy val validationProject = (project in file("validation"))
  .enablePlugins(SbtPlugin)
  .settings(
    scalaVersion := "2.12.12",
    libraryDependencies ++= BuildConfig.monocleDependencies
  )