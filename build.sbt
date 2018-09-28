name := "random-saying-generator"
 
version := "1.0" 
      
lazy val `random-saying-generator` = (project in file(".")).enablePlugins(PlayJava)
      
scalaVersion := "2.12.6"

libraryDependencies += guice
