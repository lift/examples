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
package bootstrap.liftweb

import _root_.net.liftweb._
import common.{Box, Full, Empty, Failure}
import util.{Helpers, Log, NamedPF, Props}
import http._
import actor._
import provider._
import sitemap._
import Helpers._

import example._
import widgets.autocomplete._
import comet._
import model._
import lib._
import _root_.net.liftweb.mapper.{DB, ConnectionManager, Schemifier, DefaultConnectionIdentifier, ConnectionIdentifier}

import _root_.java.sql.{Connection, DriverManager}
import snippet._


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {

    DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)
    LiftRules.addToPackages("net.liftweb.example")

    LiftRules.localeCalculator = r => definedLocale.openOr(LiftRules.defaultLocaleCalculator(r))

    if (!Props.inGAE) {
      // No DB stuff in GAE
      Schemifier.schemify(true, Log.infoF _, User, WikiEntry, Person)
    }

    WebServices.init()

    XmlServer.init()

    LiftRules.statelessDispatchTable.append {
      case r@Req("stateless" :: _, "", GetRequest) => StatelessHtml.render(r) _
    }

    LiftRules.dispatch.prepend(NamedPF("Login Validation") {
      case Req("login" :: page, "", _)
        if !LoginStuff.is && page.head != "validate" =>
        () => Full(RedirectResponse("/login/validate"))
    })

    LiftRules.snippetDispatch.append(NamedPF("Template")
              (Map("Template" -> Template,
      "AllJson" -> AllJson)))

    LiftRules.snippetDispatch.append {
      case "MyWizard" => MyWizard
      case "WizardChallenge" => WizardChallenge
      case "ScreenForm" => PersonScreen
    }

    LiftRules.snippetDispatch.append(Map("runtime_stats" -> RuntimeStats))

    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart =
            Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    /*
     * Make the spinny image go away when it ends
     */
    LiftRules.ajaxEnd =
            Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.early.append(makeUtf8)

    LiftSession.onBeginServicing = RequestLogger.beginServicing _ ::
            LiftSession.onBeginServicing

    LiftSession.onEndServicing = RequestLogger.endServicing _ ::
            LiftSession.onEndServicing

    LiftRules.setSiteMap(SiteMap(MenuInfo.menu: _*))

    ThingBuilder.boot()

    AutoComplete.init()

    // Dump information about session every 10 minutes
    SessionMaster.sessionWatchers = SessionInfoDumper ::
            SessionMaster.sessionWatchers

    // Dump browser information each time a new connection is made
    LiftSession.onBeginServicing = BrowserLogger.haveSeenYou _ :: LiftSession.onBeginServicing

  }

  private def makeUtf8(req: HTTPRequest): Unit = {req.setCharacterEncoding("UTF-8")}
}

object RequestLogger {
  object startTime extends RequestVar(0L)

  def beginServicing(session: LiftSession, req: Req) {
    startTime(millis)
  }

  def endServicing(session: LiftSession, req: Req,
                   response: Box[LiftResponse]) {
    val delta = millis - startTime.is
    Log.info("At " + (timeNow) + " Serviced " + req.uri + " in " + (delta) + "ms " + (
            response.map(r => " Headers: " + r.toResponse.headers) openOr ""
            ))
  }
}

object MenuInfo {
  import Loc._

  def menu: List[Menu] = Menu(Loc("home", List("index"), "Home")) ::
          Menu(Loc("Interactive", List("interactive"), "Interactive Stuff"),
            Menu(Loc("chat", List("chat"), "Comet Chat", Unless(() => Props.inGAE, "Disabled for GAE"))),
            Menu(Loc("longtime", List("longtime"), "Updater", Unless(() => Props.inGAE, "Disabled for GAE"))),
            Menu(Loc("ajax", List("ajax"), "AJAX Samples")),
            Menu(Loc("ajax form", List("ajax-form"), "AJAX Form")),
            Menu(Loc("js confirm", List("rhodeisland"), "Modal Dialog")),
            Menu(Loc("json", List("json"), "JSON Messaging")),
            Menu(Loc("json_more", List("json_more"), "More JSON")),
            Menu(Loc("form_ajax", List("form_ajax"), "Ajax and Forms"))
            ) ::
          Menu(Loc("Persistence", List("persistence"), "Persistence", Unless(() => Props.inGAE, "Disabled for GAE")),
            Menu(Loc("xml fun", List("xml_fun"), "XML Fun", Unless(() => Props.inGAE, "Disabled for GAE"))),
            Menu(Loc("database", List("database"), "Database", Unless(() => Props.inGAE, "Disabled for GAE"))),
            Menu(Loc("simple", Link(List("simple"), true, "/simple/index"),
              "Simple Forms", Unless(() => Props.inGAE, "Disabled for GAE"))),
            Menu(Loc("template", List("template"), "Templates", Unless(() => Props.inGAE, "Disabled for GAE")))) ::
          Menu(Loc("Templating", List("templating", "index"), "Templating"),
            Menu(Loc("Surround", List("templating", "surround"), "Surround")),
            Menu(Loc("Embed", List("templating", "embed"), "Embed")),
            Menu(Loc("eval-order", List("templating", "eval_order"), "Evalutation Order")),
            Menu(Loc("select-o-matuc", List("templating", "selectomatic"), "Select <div>s")),
            Menu(Loc("Simple Wizard", List("simple_wizard"), "Simple Wizard")),
            Menu(Loc("head", List("templating", "head"), "<head/> tag"))) ::
          Menu(Loc("ws", List("ws"), "Web Services", Unless(() => Props.inGAE, "Disabled for GAE"))) ::
          Menu(Loc("lang", List("lang"), "Localization")) ::
          Menu(Loc("menu_top", List("menu", "index"), "Menus"),
            Menu(Loc("menu_one", List("menu", "one"), "First Submenu")),
            Menu(Loc("menu_two", List("menu", "two"), "Second Submenu (has more)"),
              Menu(Loc("menu_two_one", List("menu", "two_one"),
                "First (2) Submenu")),
              Menu(Loc("menu_two_two", List("menu", "two_two"),
                "Second (2) Submenu"))
              ),
            Menu(Loc("menu_three", List("menu", "three"), "Third Submenu")),
            Menu(Loc("menu_four", List("menu", "four"), "Forth Submenu"))
            ) ::
          Menu(WikiStuff) ::
          Menu(Loc("Misc", List("misc"), "Misc code"),
            Menu(Loc("guess", List("guess"), "Number Guessing")),
            Menu(Loc("Wiz", List("wiz"), "Wizard")),
            Menu(Loc("Wiz2", List("wiz2"), "Wizard Challenge")),
            Menu(Loc("Simple Screen", List("simple_screen"), "Simple Screen")),
            Menu(Loc("arc", List("arc"), "Arc Challenge #1")),
            Menu(Loc("file_upload", List("file_upload"), "File Upload")),
            Menu(Loc("login", Link(List("login"), true, "/login/index"),
              <xml:group>Requiring Login<strike>SiteMap</strike> </xml:group>)),
            Menu(Loc("count", List("count"), "Counting"))) ::
          Menu(Loc("lift", ExtLink("http://liftweb.net"),
            <xml:group> <i>Lift</i>project home</xml:group>)) ::
          Nil
}

/**
 * Database connection calculation
 */
object DBVendor extends ConnectionManager {
  private var pool: List[Connection] = Nil
  private var poolSize = 0
  private val maxPoolSize = 4

  private lazy val chooseDriver = Props.mode match {
    case Props.RunModes.Production => "org.apache.derby.jdbc.EmbeddedDriver"
    case _ => "org.h2.Driver"
  }


  private lazy val chooseURL = Props.mode match {
    case Props.RunModes.Production => "jdbc:derby:lift_example;create=true"
    case _ => "jdbc:h2:mem:lift;DB_CLOSE_DELAY=-1"
  }


  private def createOne: Box[Connection] = try {
    val driverName: String = Props.get("db.driver") openOr chooseDriver


    val dbUrl: String = Props.get("db.url") openOr chooseURL


    Class.forName(driverName)

    val dm = (Props.get("db.user"), Props.get("db.password")) match {
      case (Full(user), Full(pwd)) =>
        DriverManager.getConnection(dbUrl, user, pwd)

      case _ => DriverManager.getConnection(dbUrl)
    }

    Full(dm)
  } catch {
    case e: Exception => e.printStackTrace; Empty
  }

  def newConnection(name: ConnectionIdentifier): Box[Connection] =
    synchronized {
      pool match {
        case Nil if poolSize < maxPoolSize =>
          val ret = createOne
          poolSize = poolSize + 1
          ret.foreach(c => pool = c :: pool)
          ret

        case Nil => wait(1000L); newConnection(name)
        case x :: xs => try {
          x.setAutoCommit(false)
          Full(x)
        } catch {
          case e => try {
            pool = xs
            poolSize = poolSize - 1
            x.close
            newConnection(name)
          } catch {
            case e => newConnection(name)
          }
        }
      }
    }

  def releaseConnection(conn: Connection): Unit = synchronized {
    pool = conn :: pool
    notify
  }
}

object BrowserLogger {
  object HaveSeenYou extends SessionVar(false)

  def haveSeenYou(session: LiftSession, request: Req) {
    if (!HaveSeenYou.is) {
      Log.info("Created session " + session.uniqueId + " IP: {" + request.request.remoteAddress + "} UserAgent: {{" + request.userAgent.openOr("N/A") + "}}")
      HaveSeenYou(true)
    }
  }
}

object SessionInfoDumper extends LiftActor {
  private var lastTime = millis

  val tenMinutes: Long = 10 minutes

  protected def messageHandler =
    {
      case SessionWatcherInfo(sessions) =>
        if ((millis - tenMinutes) > lastTime) {
          lastTime = millis
          val rt = Runtime.getRuntime
          rt.gc

          RuntimeStats.lastUpdate = timeNow
          RuntimeStats.totalMem = rt.totalMemory
          RuntimeStats.freeMem = rt.freeMemory
          RuntimeStats.sessions = sessions.size

          val dateStr: String = timeNow.toString
          Log.info("[MEMDEBUG] At " + dateStr + " Number of open sessions: " + sessions.size)
          Log.info("[MEMDEBUG] Free Memory: " + pretty(rt.freeMemory))
          Log.info("[MEMDEBUG] Total Memory: " + pretty(rt.totalMemory))
        }
    }


  private def pretty(in: Long): String =
    if (in > 1000L) pretty(in / 1000L) + "," + (in % 1000L)
    else in.toString
}
