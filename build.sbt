name := """restful-API"""
version := "1.0"
scalaVersion := "2.13.13"

val Http4sVersion = "1.0.0-M21"
val CirceVersion = "0.14.0-M5"

libraryDependencies ++= Seq(
  "org.http4s"  %% "http4s-circe"         % Http4sVersion, // Serialization library based on Circe
  "org.http4s"  %% "http4s-dsl"           % Http4sVersion, // Library for extension methods
  "org.http4s"  %% "http4s-blaze-server"  % Http4sVersion, // accepts HTTP requests
  "io.circe"    %% "circe-generic"        % CirceVersion,
)
