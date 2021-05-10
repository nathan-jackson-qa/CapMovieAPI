import play.sbt.routes.RoutesKeys

name := "ScalaMoviesApi"
 
version := "2.8"
      
lazy val `recipies_new_play_version` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.13.5"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

libraryDependencies ++= Seq(
// Enable reactive mongo for Play 2.8
"org.reactivemongo" %% "play2-reactivemongo" % "0.20.13-play28",
// Provide JSON serialization for reactive mongo
"org.reactivemongo" %% "reactivemongo-play-json-compat" % "1.0.1-play28",
// Provide BSON serialization for reactive mongo
"org.reactivemongo" %% "reactivemongo-bson-compat" % "0.20.13",
// Provide JSON serialization for Joda-Time
"com.typesafe.play" %% "play-json-joda" % "2.7.4"
)
PlayKeys.devSettings := Seq("play.server.http.port" -> "9001")
RoutesKeys.routesImport += "play.modules.reactivemongo.PathBindables._"
