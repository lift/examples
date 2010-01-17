/*
 * Copyright 2007-2010 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package webapptest

import _root_.org.mortbay.jetty.Server
import net.liftweb.example.snippet.MyWizard
import net.liftweb.http.{S, LiftSession}
import org.specs.runner.Runner
import org.specs.runner.JUnit
import org.specs.Specification
import net.liftweb.util.Helpers
import net.liftweb.common.Empty
//import _root_.org.mortbay.jetty.servlet.Context
import _root_.org.mortbay.jetty.servlet.ServletHolder
import _root_.org.mortbay.jetty.webapp.WebAppContext

import _root_.net.sourceforge.jwebunit.junit.WebTester
import _root_.junit.framework.AssertionFailedError

object JettyTestServer {
  private val serverPort_ = System.getProperty("SERVLET_PORT", "8989").toInt
  private var baseUrl_ = "http://127.0.0.1:" + serverPort_

  private val server_ : Server = {
    val server = new Server(serverPort_)
    val context = new WebAppContext()
    context.setServer(server)
    context.setContextPath("/")
    context.setWar("src/main/webapp")
    //val context = new Context(_server, "/", Context.SESSIONS)
    //context.addFilter(new FilterHolder(new LiftFilter()), "/");
    server.addHandler(context)
    server
  }

  def urlFor(path: String) = baseUrl_ + path

  def start() = server_.start()

  def stop() = {
    server_.stop()
    server_.join()
  }

  def browse(startPath: String, f: (WebTester) => Unit) = {
    val wc = new WebTester()
    try {
      wc.setScriptingEnabled(false)
      wc.beginAt(JettyTestServer.urlFor(startPath))
      f(wc)
    } catch {
      case exc: AssertionFailedError => {
        System.err.println("serveur response: ", wc.getServeurResponse())
        throw exc
      }
    } finally {
      wc.closeBrowser()
    }
  }

}

class WizardTest extends Runner(WizardSpec) with JUnit
object WizardSpec extends Specification {
  val session: LiftSession = new LiftSession("", Helpers.randomString(20), Empty)

  "A Wizard can be defined" in {
    MyWizard.nameAndAge.screenName must_== "Screen 1"

    MyWizard.favoritePet.screenName must_== "Screen 3"
  }

  "A field must have a correct Manifest" in {
    MyWizard.nameAndAge.age.manifest.erasure.getName must_== classOf[Int].getName
  }

  "A wizard must transition from first screen to second screen" in {
    S.initIfUninitted(session) {
      MyWizard.currentScreen.open_! must_== MyWizard.nameAndAge

      MyWizard.nextScreen

      MyWizard.currentScreen.open_! must_== MyWizard.nameAndAge

      MyWizard.nameAndAge.name.set("David")
      MyWizard.nameAndAge.age.set(14)

      MyWizard.nextScreen

      MyWizard.currentScreen.open_! must_== MyWizard.parentName

      MyWizard.prevScreen

      MyWizard.currentScreen.open_! must_== MyWizard.nameAndAge

      MyWizard.nameAndAge.age.set(45)

      MyWizard.nextScreen

      MyWizard.currentScreen.open_! must_== MyWizard.favoritePet

      S.clearCurrentNotices

      MyWizard.favoritePet.petName.set("Elwood")

      MyWizard.nextScreen

      MyWizard.currentScreen must_== Empty

      MyWizard.completeInfo.is must_== true
    }
  }

  "A wizard must be able to snapshot itself" in {
    val ss = S.initIfUninitted(session) {
      MyWizard.currentScreen.open_! must_== MyWizard.nameAndAge

      MyWizard.nextScreen

      MyWizard.currentScreen.open_! must_== MyWizard.nameAndAge

      MyWizard.nameAndAge.name.set("David")
      MyWizard.nameAndAge.age.set(14)

      MyWizard.nextScreen

      MyWizard.currentScreen.open_! must_== MyWizard.parentName

      MyWizard.createSnapshot
    }

    S.initIfUninitted(session) {
      MyWizard.currentScreen.open_! must_== MyWizard.nameAndAge


    }


    S.initIfUninitted(session) {
      ss.restore()

      MyWizard.prevScreen

      MyWizard.currentScreen.open_! must_== MyWizard.nameAndAge

      MyWizard.nameAndAge.age.set(45)

      MyWizard.nextScreen

      MyWizard.currentScreen.open_! must_== MyWizard.favoritePet

      S.clearCurrentNotices

      MyWizard.favoritePet.petName.set("Elwood")

      MyWizard.nextScreen

      MyWizard.currentScreen must_== Empty

      MyWizard.completeInfo.is must_== true
    }
  }
}
