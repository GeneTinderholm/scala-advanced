name := "rock-the-jvm-scala-advanced"

version := "0.1"

scalaVersion := "2.13.4"

// uncomment the next time it can't find something
// ThisBuild / useCoursier := false
libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.0"
// https://mvnrepository.com/artifact/org.scala-lang/scala-reflect
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
