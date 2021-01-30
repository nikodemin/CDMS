addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.7.6")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.0-RC4")

libraryDependencies += "com.thesamet.scalapb.zio-grpc" %% "zio-grpc-codegen" % "0.4.2"