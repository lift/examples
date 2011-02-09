package bootstrap.liftweb

import _root_.net.liftweb.common._
import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("sandbox.lift.hellodarwin")
    
    SiteMap.enforceUniqueLinks = false

    // Build SiteMap
    val entries = Menu(Loc("Home", List("index"), "Home")) ::
      Menu(Loc("Hello1.0", List("helloStatic"), "Hello Static")) ::
      Menu(Loc("Hello1.1", List("helloStatic"), "Hello Static")) ::
      Menu(Loc("Hello1.2", List("helloStatic2"), "Hello Static2")) ::
      Menu(Loc("Hello1.3", List("helloStatic3"), "Hello Static3")) ::
      Menu(Loc("Hello1.4", List("helloStatic4"), "Hello Static4")) ::
      Menu(Loc("Hello2.1", List("helloSnippet"), "Hello Snippet")) ::
      Menu(Loc("Hello2.2", List("helloSnippet2"), "Hello Snippet2")) ::
      Menu(Loc("Hello3.1", List("helloForm"), "Hello Form")) ::
      Menu(Loc("Hello3.2", List("helloForm2"), "Hello Form2")) ::
      Menu(Loc("Hello3.3", List("helloForm3"), "Hello Form3")) ::
      Menu(Loc("Hello3.4", List("helloForm4"), "Hello Form4")) ::
      Menu(Loc("Hello4.1", List("helloFormAjax"), "Hello FormAjax")) ::
      Nil
    LiftRules.setSiteMap(SiteMap(entries:_*))
  }
}

