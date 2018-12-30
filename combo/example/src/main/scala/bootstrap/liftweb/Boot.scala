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

import net.liftweb._
import common.{Box, Empty, Failure, Full, Loggable}
import util.{Helpers, NamedPF, Props}
import http._
import actor._
import provider._
import sitemap._
import Helpers._
import example._
import net.liftmodules.widgets.autocomplete._
import net.liftmodules.{fobobs4, fobofa, fobohl, fobojq, fobopop}
import comet._
import model._
import lib._
import net.liftweb.mapper.{
  ConnectionIdentifier,
  ConnectionManager,
  DB,
  DefaultConnectionIdentifier,
  Schemifier
}

import scala.xml.Text
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

    /**
      * We're doing this as HTML5
      */
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    LiftRules.localeCalculator = r =>
      definedLocale.openOr(LiftRules.defaultLocaleCalculator(r))

    if (!Props.inGAE) {
      // No DB stuff in GAE
      Schemifier.schemify(true, Schemifier.infoF _, User, WikiEntry, Person)
    }

    LiftRules.dispatch.append(WebServices)

    LiftRules.dispatch.append(AsyncRest)

    StatelessJson.init()

    XmlServer.init()

    LiftRules.statelessDispatch.append {
      case r @ Req("stateless" :: _, "", GetRequest) =>
        StatelessHtml.render(r) _
    }

    LiftRules.dispatch.prepend(NamedPF("Login Validation") {
      case Req("login" :: page, "", _)
          if !LoginStuff.is && page.head != "validate" =>
        () =>
          Full(RedirectResponse("/login/validate"))
    })

    SessionMaster.sessionCheckFuncs = SessionMaster.sessionCheckFuncs :::
      List(SessionChecker)

    // Uncomment the lines below to see how
    // a Lift app looks when it's stateless
    /*
    LiftRules.statelessTest.prepend {
      case _ => true
    }*/

    LiftRules.snippetDispatch.append(Map("runtime_stats" -> RuntimeStats))

    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart = Full(
      () => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    /*
     * Make the spinny image go away when it ends
     */
    LiftRules.ajaxEnd = Full(
      () => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.early.append(makeUtf8)

    LiftRules.cometCreation.append {
      case CometCreationInfo("Clock", name, defaultXml, attributes, session) =>
        new ExampleClock(session, Full("Clock"), name, defaultXml, attributes)

    }

    LiftRules.noticesAutoFadeOut.default.set((notices: NoticeType.Value) => {
      notices match {
        case NoticeType.Notice => Full((8 seconds, 4 seconds))
        case _                 => Empty
      }
    })

    LiftSession.onBeginServicing = RequestLogger.beginServicing _ ::
      LiftSession.onBeginServicing

    LiftSession.onEndServicing = RequestLogger.endServicing _ ::
      LiftSession.onEndServicing

    LiftRules.setSiteMapFunc(() => MenuInfo.sitemap())
    LiftRules.securityRules = () => {
      SecurityRules(
        content = Some(
          ContentSecurityPolicy(
            scriptSources = List(ContentSourceRestriction.UnsafeEval,
                                 ContentSourceRestriction.UnsafeInline,
                                 ContentSourceRestriction.Self),
            styleSources = List(ContentSourceRestriction.UnsafeInline,
                                ContentSourceRestriction.Self)
          )))
    }

    // FoBo init
    fobojq.Toolkit.init = fobojq.Toolkit.JQuery224
    fobohl.Toolkit.init = fobohl.Toolkit.HighlightJS930
    fobofa.Toolkit.init = fobofa.Toolkit.FontAwesome550
    fobobs4.Toolkit.init = fobobs4.Toolkit.Bootstrap413
    fobopop.Toolkit.init = fobopop.Toolkit.Popper1129
    fobojq.Toolkit.init = fobojq.Toolkit.JQueryMigrate141

    ThingBuilder.boot()

    AutoComplete.init()

    // Dump information about session every 10 minutes
    SessionMaster.sessionWatchers = SessionInfoDumper ::
      SessionMaster.sessionWatchers

    // Dump browser information each time a new connection is made
    LiftSession.onBeginServicing = BrowserLogger.haveSeenYou _ :: LiftSession.onBeginServicing

  }

  private def makeUtf8(req: HTTPRequest): Unit = {
    req.setCharacterEncoding("UTF-8")
  }
}

object RequestLogger extends Loggable {
  object startTime extends RequestVar(0L)

  def beginServicing(session: LiftSession, req: Req) {
    startTime(millis)
  }

  def endServicing(session: LiftSession,
                   req: Req,
                   response: Box[LiftResponse]) {
    val delta = millis - startTime.is
    logger.info(
      "At " + (now) + " Serviced " + req.uri + " in " + (delta) + "ms " + (
        response.map(r => " Headers: " + r.toResponse.headers) openOr ""
      ))
  }
}

object MenuInfo {
  import Loc._

  private lazy val noGAE = Unless(() => Props.inGAE, "Disabled for GAE")
  private val topNavLG = LocGroup("topNav")

  private val siteMapList = List(
    Menu.i("Home") / "index" >> topNavLG,
    TopNav.interactiveMenuPart,
    Menu.i("Interactive Stuff") / "interactive" submenus (
      Menu("Comet Chat") / "chat" >> noGAE,
      Menu("Ajax Samples") / "ajax",
      Menu("Ajax Form") / "ajax-form",
      Menu("Modal Dialog") / "rhodeisland",
      Menu("JSON Messaging") / "json",
      Menu("Stateless JSON Messaging") / "stateless_json",
      // Menu("More JSON") / "json_more",
      Menu("Ajax and Forms") / "form_ajax"
    ),
    TopNav.persistenceMenuPart,
    Menu.i("Persistence") / "persistence" >> noGAE submenus (
      Menu("XML Fun") / "xml_fun" >> noGAE,
      Menu("Database") / "database" >> noGAE,
      Menu(
        Loc("simple",
            Link(List("simple"), true, "/simple/index"),
            "Simple Forms",
            noGAE)) //,
      // Menu("Templates") / "template" >> noGAE
    ),
    TopNav.templatingMenuPart,
    Menu.i("Templating") / "templating" / "index" submenus (
      Menu("Surround") / "templating" / "surround",
      Menu("Embed") / "templating" / "embed",
      Menu("Evalutation Order") / "templating" / "eval_order",
      Menu("Select <div>s") / "templating" / "selectomatic",
      Menu("Simple Wizard") / "simple_wizard",
      Menu("Lazy Loading") / "lazy",
      Menu("Parallel Snippets") / "parallel",
      Menu("<head/> tag") / "templating" / "head"
    ),
    Menu.i("Web Services") / "ws" >> noGAE >> topNavLG,
    Menu.i("Localization") / "lang" >> topNavLG,
    TopNav.menusMenuPart,
    Menu.i("Menus") / "menu" / "index" submenus (
      Menu("First Submenu") / "menu" / "one",
      Menu("Second Submenu (has more)") / "menu" / "two" submenus (
        Menu("First (2) Submenu") / "menu" / "two_one",
        Menu("Second (2) Submenu") / "menu" / "two_two"
      ),
      Menu("Third Submenu") / "menu" / "three",
      Menu("Forth Submenu") / "menu" / "four"
    ),
    Menu(WikiStuff),
    TopNav.miscMenuPart,
    Menu.i("Misc code") / "misc" submenus (
      Menu("Long Time") / "longtime",
      Menu("Number Guessing") / "guess",
      Menu("Wizard") / "wiz",
      Menu("Wizard Challenge") / "wiz2",
      Menu("Simple Screen") / "simple_screen",
      Menu("Variable Screen") / "variable_screen",
      Menu("Arc Challenge #1") / "arc",
      Menu("Simple Wiring") / "simple_wiring",
      Menu("Wiring Invoice") / "invoice_wiring",
      Menu("File Upload") / "file_upload",
      Menu("Async REST") / "async_rest",
      Menu(
        Loc("login",
            Link(List("login"), true, "/login/index"),
            <xml:group>Requiring Login<strike>SiteMap</strike> </xml:group>)),
      Menu("Counting") / "count"
    ),
    Menu(
      Loc("lift",
          ExtLink("http://liftweb.net"),
          S.loc("lift", <xml:group> <i>Lift</i> project home</xml:group>),
          topNavLG)),
    Menu(
      Loc("src",
          ExtLink("https://github.com/lift/examples/tree/master/combo/example"),
          S.loc("src", Text("Source code for this site")),
          topNavLG))
  )

  private object TopNav {
    // Interactive stuff
    private val interactiveLoc = Loc(
      "topNavInteractive",
      Link(List("topNavInteractive"), true, "/interactive"),
      S.loc("topNavInteractive", Text("Interactive Stuff")))
    private val cometChatLoc = Loc("topNavCometChat",
                                   Link(List("topNavCometChat"), true, "/chat"),
                                   S.loc("topNavCometChat", Text("Comet Chat")),
                                   noGAE)
    private val ajaxSamplesLoc = Loc(
      "topNavAjaxSamples",
      Link(List("topNavAjaxSamples"), true, "/ajax"),
      S.loc("topNavAjaxSamples", Text("Ajax Samples")))
    private val ajaxFormLoc = Loc(
      "topNavAjaxForm",
      Link(List("topNavAjaxForm"), true, "/ajax-form"),
      S.loc("topNavAjaxForm", Text("Ajax Form")))
    private val modalDialogLoc = Loc(
      "topNavModalDialog",
      Link(List("topNavModalDialog"), true, "/rhodeisland"),
      S.loc("topNavModalDialog", Text("Modal Dialog")))
    private val jSONMessagingLoc = Loc(
      "topNavJSONMessaging",
      Link(List("topNavJSONMessaging"), true, "/json"),
      S.loc("topNavJSONMessaging", Text("JSON Messaging")))
    private val statelessJSONMessagingLoc = Loc(
      "topNavStatelessJSONMessaging",
      Link(List("topNavStatelessJSONMessaging"), true, "/stateless_json"),
      S.loc("topNavStatelessJSONMessaging", Text("Stateless JSON Messaging"))
    )
    private val AjaxAndFormsLoc = Loc(
      "topNavAjaxAndForms",
      Link(List("topNavAjaxAndForms"), true, "/form_ajax"),
      S.loc("topNavAjaxAndForms", Text("Ajax and Forms")))
    private val interactiveDD = Menu.i("topNavInteractiveDD") / "/dddlabel2"
    private val interactive = Menu(interactiveLoc)
    private val cometChat = Menu(cometChatLoc)
    private val ajaxSamples = Menu(ajaxSamplesLoc)
    private val ajaxForm = Menu(ajaxFormLoc)
    private val modalDialog = Menu(modalDialogLoc)
    private val jSONMessaging = Menu(jSONMessagingLoc)
    private val statelessJSONMessaging = Menu(statelessJSONMessagingLoc)
    private val ajaxAndForms = Menu(AjaxAndFormsLoc)

    // Persistence
    private val persistenceLoc = Loc(
      "topNavPersistence",
      Link(List("topNavPersistence"), true, "/persistence"),
      S.loc("topNavPersistence", Text("Persistence")),
      noGAE)
    private val xMLFunLoc = Loc("topNavXMLFun",
                                Link(List("topNavXMLFun"), true, "/xml_fun"),
                                S.loc("topNavXMLFun", Text("XML Fun")),
                                noGAE)
    private val databaseLoc = Loc(
      "topNavDatabase",
      Link(List("topNavDatabase"), true, "/database"),
      S.loc("topNavDatabase", Text("Database")),
      noGAE)
    private val simpleLoc = Loc(
      "topNavSimple",
      Link(List("topNavSimple"), true, "/simple/index"),
      S.loc("topNavSimple", Text("Simple Forms")),
      noGAE)
    private val persistenceDD = Menu.i("topNavPersistenceDD") / "/ddlabel3" >> noGAE
    private val persistence = Menu(persistenceLoc)
    private val xMLFun = Menu(xMLFunLoc)
    private val database = Menu(databaseLoc)
    private val simple = Menu(simpleLoc)

    // Templating
    private val teplatingLoc = Loc(
      "topNavTeplating",
      Link(List("topNavTeplating"), true, "/templating/index"),
      S.loc("topNavTeplating", Text("Templating"))
    )
    private val surroundLoc = Loc(
      "topNavSurround",
      Link(List("topNavSurround"), true, "/templating/surround"),
      S.loc("topNavSurround", Text("Surround"))
    )
    private val embedLoc = Loc(
      "topNavEmbed",
      Link(List("topNavEmbed"), true, "/templating/embed"),
      S.loc("topNavEmbed", Text("Embed"))
    )
    private val evaluationOrderLoc = Loc(
      "topNavEvaluationOrder",
      Link(List("topNavEvaluationOrder"), true, "/templating/eval_order"),
      S.loc("topNavEvaluationOrder", Text("Evalutation Order"))
    )
    private val selectDivsLoc = Loc(
      "tomNavSelectDivs",
      Link(List("tomNavSelectDivs"), true, "/templating/selectomatic"),
      S.loc("tomNavSelectDivs", Text("Select <div>s"))
    )
    private val simpleWizardLoc = Loc(
      "topNavSimpleWizard",
      Link(List("topNavSimpleWizard"), true, "/simple_wizard"),
      S.loc("topNavSimpleWizard", Text("Simple Wizard"))
    )
    private val lazyLoadingLoc = Loc(
      "topNavLazyLoading",
      Link(List("topNavLazyLoading"), true, "/lazy"),
      S.loc("topNavLazyLoading", Text("Lazy Loading"))
    )
    private val parallelSnippetsLoc = Loc(
      "topNavParallelSnippets",
      Link(List("topNavParallelSnippets"), true, "/parallel"),
      S.loc("topNavParallelSnippets", Text("Parallel Snippets"))
    )
    //<head/> tag
    private val headTagLoc = Loc(
      "topNavHeadTagLoc",
      Link(List("topNavHeadTagLoc"), true, "/templating/head"),
      S.loc("topNavHeadTagLoc", Text("<head/> tag"))
    )
    private val templatingDD = Menu.i("topNavTemplatingDD") / "/ddlabel4"
    private val templating = Menu(teplatingLoc)
    private val surround = Menu(surroundLoc)
    private val embed = Menu(embedLoc)
    private val evaluationOrder = Menu(evaluationOrderLoc)
    private val selectDivs = Menu(selectDivsLoc)
    private val simpleWizard = Menu(simpleWizardLoc)
    private val lazyLoading = Menu(lazyLoadingLoc)
    private val parallelSnippets = Menu(parallelSnippetsLoc)
    private val headTag = Menu(headTagLoc)

    // Menu
    private val menusLoc = Loc(
      "topNavMenus",
      Link(List("topNavMenus"), true, "/menu/index"),
      S.loc("topNavMenus", Text("Menus"))
    )
    private val firstSubmenuLoc = Loc(
      "topNavFirstSubmenu",
      Link(List("topNavFirstSubmenu"), true, "/menu/one"),
      S.loc("topNavFirstSubmenu", Text("First Submenu"))
    )
    private val secondSubmenuLoc = Loc(
      "topNavSecondSubmenu",
      Link(List("topNavSecondSubmenu"), true, "/menu/two"),
      S.loc("topNavSecondSubmenu", Text("Second Submenu (has more)"))
    )
    private val first2SubmenuLoc = Loc(
      "topNavFirst2Submenu",
      Link(List("topNavFirst2Submenu"), true, "/menu/two_one"),
      S.loc("topNavFirst2Submenu", Text("First (2) Submenu"))
    )
    private val second2SubmenuLoc = Loc(
      "topNavSecond2Submenu",
      Link(List("topNavSecond2Submenu"), true, "/menu/two_two"),
      S.loc("topNavSecond2Submenu", Text("Second (2) Submenu"))
    )
    private val thirdSubmenuLoc = Loc(
      "topNavThirdSubmenu",
      Link(List("topNavThirdSubmenu"), true, "/menu/three"),
      S.loc("topNavThirdSubmenu", Text("Third Submenu"))
    )
    private val forthSubmenuLoc = Loc(
      "topNavForthSubmenu",
      Link(List("topNavForthSubmenu"), true, "/menu/four"),
      S.loc("topNavForthSubmenu", Text("Forth Submenu"))
    )
    private val menusDD = Menu.i("topNavMenusDD") / "/ddlabel5"
    private val menus = Menu(menusLoc)
    private val firstSubmenu = Menu(firstSubmenuLoc)
    private val secondSubmenu = Menu(secondSubmenuLoc)
    private val first2Submenu = Menu(first2SubmenuLoc)
    private val second2Submenu = Menu(second2SubmenuLoc)
    private val thirdSubmenu = Menu(thirdSubmenuLoc)
    private val forthSubmenu = Menu(forthSubmenuLoc)

    // Misc
    private val miscLoc = Loc(
      "topNavMisc",
      Link(List("topNavMisc"), true, "/misc"),
      S.loc("topNavMisc", Text("Misc code"))
    )
    private val longTimeLoc = Loc(
      "topNavLongTime",
      Link(List("topNavLongTime"), true, "/longtime"),
      S.loc("topNavLongTime", Text("Long Time"))
    )
    private val numberGuessingLoc = Loc(
      "topNavNumberGuessing",
      Link(List("topNavNumberGuessing"), true, "/guess"),
      S.loc("topNavNumberGuessing", Text("Number Guessing"))
    )
    private val wizardLoc = Loc(
      "topNavWizard",
      Link(List("topNavWizard"), true, "/wiz"),
      S.loc("topNavWizard", Text("Wizard"))
    )
    private val wizardChallengeLoc = Loc(
      "topNavWizardChallenge",
      Link(List("topNavWizardChallenge"), true, "/wiz2"),
      S.loc("topNavWizardChallenge", Text("Wizard Challenge"))
    )
    private val simpleScreenLoc = Loc(
      "topNavSimpleScreen",
      Link(List("topNavSimpleScreen"), true, "/simple_screen"),
      S.loc("topNavSimpleScreen", Text("Simple Screen"))
    )
    private val variableScreenLoc = Loc(
      "topNavVariableScreen",
      Link(List("topNavVariableScreen"), true, "/variable_screen"),
      S.loc("topNavVariableScreen", Text("Variable Screen"))
    )
    private val arcChallenge1Loc = Loc(
      "topNavArcChallenge1",
      Link(List("topNavArcChallenge1"), true, "/arc"),
      S.loc("topNavArcChallenge1", Text("Arc Challenge #1"))
    )
    private val simpleWiringLoc = Loc(
      "topNavSimpleWiring",
      Link(List("topNavSimpleWiring"), true, "/simple_wiring"),
      S.loc("topNavSimpleWiring", Text("Simple Wiring"))
    )
    private val wiringInvoiceLoc = Loc(
      "topNavWiringInvoice",
      Link(List("topNavWiringInvoice"), true, "/invoice_wiring"),
      S.loc("topNavWiringInvoice", Text("Wiring Invoice"))
    )
    private val fileUploadLoc = Loc(
      "topNavFileUpload",
      Link(List("topNavFileUpload"), true, "/file_upload"),
      S.loc("topNavFileUpload", Text("File Upload"))
    )
    private val asyncRESTLoc = Loc(
      "topNavAsyncREST",
      Link(List("topNavAsyncREST"), true, "/async_rest"),
      S.loc("topNavAsyncREST", Text("Async REST"))
    )
    private val loginLoc = Loc(
      "topNavLogin",
      Link(List("topNavLogin"), true, "/login/index"),
      S.loc("topNavLogin", <xml:group>Requiring Login<strike>SiteMap</strike> </xml:group>)
    )
    private val countingLoc = Loc(
      "topNavCounting",
      Link(List("topNavCounting"), true, "/count"),
      S.loc("topNavCounting", Text("Counting"))
    )
    private val miscDD = Menu.i("topNavMiscDD") / "/ddlabel6"
    private val misc = Menu(miscLoc)
    private val longTime = Menu(longTimeLoc)
    private val numberGuessing = Menu(numberGuessingLoc)
    private val wizard = Menu(wizardLoc)
    private val simpleScreen = Menu(simpleScreenLoc)
    private val variableScreen = Menu(variableScreenLoc)
    private val arcChallenge = Menu(arcChallenge1Loc)
    private val simpleWiring = Menu(simpleWiringLoc)
    private val wiringInvoice = Menu(wiringInvoiceLoc)
    private val fileUpload = Menu(fileUploadLoc)
    private val asyncRest = Menu(asyncRESTLoc)
    private val login = Menu(loginLoc)
    private val counting = Menu(countingLoc)

    // Public stuff
    val interactiveMenuPart = interactiveDD >> topNavLG >> PlaceHolder submenus (interactive,
    cometChat, ajaxSamples, ajaxForm, modalDialog, jSONMessaging, statelessJSONMessaging, ajaxAndForms)

    val persistenceMenuPart = persistenceDD >> topNavLG >> PlaceHolder submenus (persistence,
    xMLFun, database, simple)

    val templatingMenuPart = templatingDD >> topNavLG >> PlaceHolder submenus (templating,
    surround, embed, evaluationOrder, selectDivs, simpleWizard, lazyLoading, parallelSnippets, headTag)

    // Note: The bootstrap navigator dose only handle one level of sub-menus
    val menusMenuPart = menusDD >> topNavLG >> PlaceHolder submenus (menus,
    firstSubmenu, secondSubmenu, first2Submenu, second2Submenu, thirdSubmenu, forthSubmenu)

    val miscMenuPart = miscDD >> topNavLG >> PlaceHolder submenus (misc, longTime, numberGuessing,
    wizard, simpleScreen, variableScreen, arcChallenge, simpleWiring, wiringInvoice, fileUpload, asyncRest,
    login, counting)
  }

  def sitemap() = SiteMap(siteMapList: _*)
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
    case _                         => "org.h2.Driver"
  }

  private lazy val chooseURL = Props.mode match {
    case Props.RunModes.Production => "jdbc:derby:lift_example;create=true"
    case _                         => "jdbc:h2:mem:lift;DB_CLOSE_DELAY=-1"
  }

  private def createOne: Box[Connection] =
    try {
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
        case x :: xs =>
          try {
            x.setAutoCommit(false)
            Full(x)
          } catch {
            case e =>
              try {
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

object BrowserLogger extends Loggable {
  object HaveSeenYou extends SessionVar(false)

  def haveSeenYou(session: LiftSession, request: Req) {
    if (!HaveSeenYou.is) {
      logger.info(
        "Created session " + session.uniqueId + " IP: {" + request.request.remoteAddress + "} UserAgent: {{" + request.userAgent
          .openOr("N/A") + "}}")
      HaveSeenYou(true)
    }
  }
}

object SessionInfoDumper extends LiftActor with Loggable {
  private var lastTime = millis

  private def cyclePeriod = 10 minute

  import net.liftweb.example.lib.SessionChecker

  protected def messageHandler = {
    case SessionWatcherInfo(sessions) =>
      if ((millis - cyclePeriod) > lastTime) {
        lastTime = millis
        val rt = Runtime.getRuntime
        rt.gc

        RuntimeStats.lastUpdate = now
        RuntimeStats.totalMem = rt.totalMemory
        RuntimeStats.freeMem = rt.freeMemory
        RuntimeStats.sessions = sessions.size

        val percent = (RuntimeStats.freeMem * 100L) / RuntimeStats.totalMem

        // get more aggressive about purging if we're
        // at less than 35% free memory
        if (percent < 35L) {
          SessionChecker.killWhen /= 2L
          if (SessionChecker.killWhen < 5000L)
            SessionChecker.killWhen = 5000L
          SessionChecker.killCnt *= 2
        } else {
          SessionChecker.killWhen *= 2L
          if (SessionChecker.killWhen >
                SessionChecker.defaultKillWhen)
            SessionChecker.killWhen = SessionChecker.defaultKillWhen
          val newKillCnt = SessionChecker.killCnt / 2
          if (newKillCnt > 0) SessionChecker.killCnt = newKillCnt
        }

        val dateStr: String = now.toString
        logger.info(
          "[MEMDEBUG] At " + dateStr + " Number of open sessions: " + sessions.size)
        logger.info("[MEMDEBUG] Free Memory: " + pretty(RuntimeStats.freeMem))
        logger.info("[MEMDEBUG] Total Memory: " + pretty(RuntimeStats.totalMem))
        logger.info(
          "[MEMDEBUG] Kill Interval: " + (SessionChecker.killWhen / 1000L))
        logger.info("[MEMDEBUG] Kill Count: " + (SessionChecker.killCnt))
      }
  }

  private def pretty(in: Long): String =
    if (in > 1000L) pretty(in / 1000L) + "," + (in % 1000L)
    else in.toString
}
