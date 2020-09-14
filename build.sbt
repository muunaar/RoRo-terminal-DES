name := "RoRO DES"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "2.1.3",
  "co.fs2" %% "fs2-core" % "2.2.1",
  "com.softwaremill.common" %% "id-generator" % "1.2.1",
  "org.typelevel" %% "cats-core" % "2.1.1",
  "org.scalatest" %% "scalatest" % "3.1.2" % "test",
  "io.chrisdavenport" %% "log4cats-slf4j" % "1.1.1",
  "io.chrisdavenport" %% "log4cats-core" % "1.1.1",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "co.fs2" %% "fs2-io" % "2.4.0"
)
