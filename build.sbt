lazy val coreProject = (project in file("."))
  .enablePlugins(CalibanPlugin)
  .settings(
    scalaVersion := "2.13.6",
    name := "CDMS",
    version := "0.1.0",
    libraryDependencies ++= Dependencies.live,
    scalacOptions += "-Ymacro-annotations",
    Compile / caliban / calibanSettings ++= Settings.calibanSettings,
    Compile / caliban / calibanSources := Settings.calibanSourcesRoot,
    Test / fork := true
  )
