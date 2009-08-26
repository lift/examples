package sandbox.lift.hellodarwin.snippet

import _root_.scala.xml.NodeSeq
import _root_.net.liftweb.http.S._
import _root_.net.liftweb.http.SHtml._
import _root_.net.liftweb.http.RequestVar
import _root_.net.liftweb.util.Helpers._
import _root_.net.liftweb.util.Full

class HelloForm2 {
  object who extends RequestVar(Full("world"))

  def show(xhtml: NodeSeq): NodeSeq = {
    bind("hello", xhtml,
        "whoField" -> text(who.openOr(""), v => who(Full(v))) % ("size" -> "10") % ("id" -> "whoField"),
        "submit" -> submit(?("Send"), () => println("value:" + who.openOr("") + " :: " + param("whoField"))),
        "who" -> who.openOr("")
    )
  }
}

