import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "ncr"
  val appVersion      = "1.0"    

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    "com.typesafe.slick" %% "slick" % "1.0.0",
    //"postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    "org.mindrot" % "jbcrypt" % "0.3m",
    "joda-time" % "joda-time" % "2.0",
    "org.joda" % "joda-convert" % "1.1",
    "com.typesafe" %% "play-plugins-mailer" % "2.1-RC2"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )
}
