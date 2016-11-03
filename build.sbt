name := "circe-test"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % "0.5.4",
  "io.circe" %% "circe-generic" % "0.5.4",
  "io.circe" %% "circe-parser" % "0.5.4",
  "io.circe" %% "circe-java8" % "0.5.4"
)