/*
 * Copyright 2010-2011 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import net.liftweb.sbt._
import sbt._


class LiftExamplesProject(info: ProjectInfo) extends ParentProject(info) with LiftParentProject {

  // TODO: consider cross-lift build, for now set it to current project version
  val liftVersion = version.toString
  
  object LiftDependencies {
    // Lift dependencies
    lazy val lift_common = "net.liftweb" %% "lift-common" % liftVersion
    lazy val lift_actor  = "net.liftweb" %% "lift-actor" % liftVersion
    lazy val lift_json   = "net.liftweb" %% "lift-jason" % liftVersion
    lazy val lift_util   = "net.liftweb" %% "lift-util" % liftVersion

    lazy val lift_webkit = "net.liftweb" %% "lift-webkit" % liftVersion
    lazy val lift_wizard = "net.liftweb" %% "lift-wizard" % liftVersion

    lazy val lift_db     = "net.liftweb" %% "lift-db" % liftVersion
    lazy val lift_mapper = "net.liftweb" %% "lift-mapper" % liftVersion

    lazy val lift_facebook = "net.liftweb" %% "lift-facebook" % liftVersion
    lazy val lift_scalate  = "net.liftweb" %% "lift-scalate" % liftVersion
    lazy val lift_textile  = "net.liftweb" %% "lift-textile" % liftVersion
    lazy val lift_widgets  = "net.liftweb" %% "lift-widgets" % liftVersion
  }

  import CompileScope._
  import ProvidedScope._
  import LiftDependencies._


  // Combo projects
  // --------------
  lazy val example = comboProject("example", lift_wizard, lift_mapper, lift_textile, lift_widgets, RuntimeScope.h2database)()
  lazy val liftj   = comboProject("liftj", lift_wizard)()


  // Nuggets projects
  // ----------------
  lazy val flot         = nuggetsProject("flotDemo", lift_widgets)()
  lazy val hellodarwin  = nuggetsProject("hellodarwin", lift_mapper)()
  lazy val hellofbc     = nuggetsProject("hellofbc", lift_mapper, lift_widgets, lift_facebook, servlet_api)()
  lazy val hellolift    = nuggetsProject("hellolift", lift_mapper)()
  lazy val helloscalate = nuggetsProject("helloscalate", lift_mapper, lift_scalate)()
  lazy val httpauth     = nuggetsProject("http-authentication", lift_webkit)()
  // lazy val jpademo      = nuggetsProject("JPADemo", lift_mapper, lift_scalate)()
  lazy val skittr       = nuggetsProject("skittr", lift_mapper)()


  // Examples apidocs
  // ----------------
  // lazy val examples_doc = project(".", "lift-examples-doc", new DefaultProject(_) with LiftDefaultDocProject)


  private def comboProject = examplesProject("combo") _
  private def nuggetsProject = examplesProject("nuggets") _

  private def examplesProject(base: String)(path: String, libs: ModuleID*)(deps: Project*) =
    project(base / path, "lift-" + path, new ExamplesProject(_, libs: _*), deps: _*)


  // Default base
  // ------------
  class ExamplesProject(info: ProjectInfo, libs: ModuleID*) extends DefaultWebProject(info) with LiftDefaultWebProject {

    import TestScope._

    override def libraryDependencies = super.libraryDependencies ++ libs ++ Seq(jetty6, junit, jwebunit, mockito_all)

    // TODO: Remove these and resort to LiftDefaultProject settings
    override def compileOptions = Seq("-Xwarninit", "-encoding", "utf8").map(CompileOption)

    // System property hack for derby.log, webapptests
    override def testAction =
      super.testAction dependsOn
      task {
        System.setProperty("derby.stream.error.file", (outputPath / "derby.log").absString)
        System.setProperty("net.liftweb.webapptest.src.test.webapp", (testSourcePath / "webapp").absString)
        None
      }

    // FIXME: breaks with SBT
    override def testOptions =
      ExcludeTests(
        // example wiki
        "webapptest.WikiUsages" :: Nil) ::
      super.testOptions.toList
  }

}
