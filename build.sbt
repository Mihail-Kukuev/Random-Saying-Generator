name := "random-saying-generator"
 
version := "1.0" 
      
lazy val `random-saying-generator` = (project in file(".")).enablePlugins(PlayJava)
      
scalaVersion := "2.12.6"

libraryDependencies += guice
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.10"
