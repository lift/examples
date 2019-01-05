//##################################################################
//##
//##  Build settings
//##
//##############

lazy val projectSettings = Seq(
  organization := "net.liftweb",
  version := "0.9.3-SNAPSHOT",
  name := "demo",
  scalaVersion := "2.12.7",
  scalacOptions ++= Seq("-unchecked", "-deprecation"),
  autoAPIMappings := true
)

lazy val meta = (project in file("."))
  .enablePlugins(JettyPlugin)
  .enablePlugins(BuildInfoPlugin)
  .settings(projectSettings: _*)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoKeys ++= Seq[BuildInfoKey](
      BuildInfoKey.action("buildTime") {
        System.currentTimeMillis
      } // re-computed each time at compile
    ),
    buildInfoPackage := "net.liftweb.example.lib"
  )

//##
//##
//##################################################################

//##################################################################
//##
//##  Resolvers
//##
//##############


resolvers ++= Seq("snapshots"     at "https://oss.sonatype.org/content/repositories/snapshots",
                  "staging"       at "https://oss.sonatype.org/content/repositories/staging",
                  "releases"      at "https://oss.sonatype.org/content/repositories/releases"
)

//##
//##
//##################################################################

//##################################################################
//##
//##  Dependencies
//##
//##############

libraryDependencies ++= {
  val liftVersion = "3.3.0"
  Seq(
    "net.liftweb" %% "lift-webkit" % liftVersion,
    "net.liftweb" %% "lift-json" % liftVersion,
    "net.liftweb" %% "lift-db" % liftVersion,
    "net.liftweb" %% "lift-mapper" % liftVersion,

    "net.liftmodules" %% "fobo-twbs-bootstrap4-api_3.3" % "2.1.1",
    "net.liftmodules" %% "widgets_3.1" % "1.6.0-SNAPSHOT",
    "net.liftmodules" %% "textile_3.1" % "1.4-SNAPSHOT",
    
    "org.webjars" % "bootstrap" % "4.2.1",
    "org.webjars" % "jquery" % "3.0.0",
    "org.webjars" % "jquery-migrate" % "1.4.1",
    "org.webjars" % "popper.js" % "1.14.3",
    "org.webjars" % "font-awesome" % "5.6.1",
    "org.webjars" % "highlightjs" % "9.6.0",
    
    "org.eclipse.jetty" % "jetty-webapp" % "8.1.7.v20120910" % "test",
    "junit" % "junit" % "4.10" % "test",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.specs2" %% "specs2-core" % "3.8.6" % "test",
    "org.specs2" %% "specs2-matcher-extra" % "3.8.6" % "test",
    "org.specs2" %% "specs2-junit" % "3.8.6" % "test",
    "com.h2database" % "h2" % "1.3.167"
  )
}

//##
//##
//##################################################################