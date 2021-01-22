name := "CDMS"

version := "0.1"

scalaVersion := "2.13.4"

libraryDependencies ++= BuildConfig.projectDependencies


dockerBaseImage := "adoptopenjdk/openjdk15:alpine"

dockerExposedPorts += 9002

enablePlugins(JavaAppPackaging, DockerPlugin)