enablePlugins(JettyPlugin)

organization := "net.liftweb"

name := "demo"

version := "0.6.1-SNAPSHOT"

scalaVersion := "2.11.11" //2.12.2

scalacOptions ++= Seq("-deprecation", "-unchecked")

resolvers ++= Seq("snapshots"     at "https://oss.sonatype.org/content/repositories/snapshots",
                  "staging"       at "https://oss.sonatype.org/content/repositories/staging",
                  "releases"      at "https://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= {
  val liftVersion = "3.3.0"
  Seq(
    "net.liftweb" %% "lift-webkit" % liftVersion,
    "net.liftweb" %% "lift-json" % liftVersion,
    "net.liftweb" %% "lift-db" % liftVersion,
    "net.liftweb" %% "lift-mapper" % liftVersion,
    "net.liftmodules" %% "fobo_3.3" % "2.1.0-SNAPSHOT",
    "net.liftmodules" %% "widgets_3.1" % "1.6.0-SNAPSHOT",
    "net.liftmodules" %% "textile_3.1" % "1.4-SNAPSHOT",
    "org.eclipse.jetty" % "jetty-webapp" % "8.1.7.v20120910" % "test",
    "junit" % "junit" % "4.10" % "test",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.specs2" %% "specs2-core" % "3.8.6" % "test",
    "org.specs2" %% "specs2-matcher-extra" % "3.8.6" % "test",
    "org.specs2" %% "specs2-junit" % "3.8.6" % "test",
    "com.h2database" % "h2" % "1.3.167"
  )
}