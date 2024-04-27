name := """restful-API"""
version := "1.0"
scalaVersion := "2.13.13"

val Http4sVersion = "1.0.0-M21"
val CirceVersion = "0.14.0-M5"
val JDBCVersion = "8.0.28"
val Slf4jVersion = "2.0.13"

libraryDependencies ++= Seq(
  "org.http4s"  %% "http4s-circe"         % Http4sVersion, // Serialization library based on Circe
  "org.http4s"  %% "http4s-dsl"           % Http4sVersion, // Library for extension methods
  "org.http4s"  %% "http4s-blaze-server"  % Http4sVersion, // accepts HTTP requests
  "io.circe"    %% "circe-generic"        % CirceVersion,
  "mysql"       % "mysql-connector-java"  % JDBCVersion, // mysql jdbc connector driver
  "org.slf4j"   % "slf4j-simple"          % Slf4jVersion, // logger for mysql/jdbc connector, wouldn't be needed for slf4j-api version 2.x
)

ThisBuild / assemblyMergeStrategy := {
  case "META-INF/versions/9/module-info.class" => MergeStrategy.first
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}

assembly / mainClass := Some("app.Http4sTutorial")