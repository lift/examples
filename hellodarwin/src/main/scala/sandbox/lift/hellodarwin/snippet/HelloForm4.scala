package sandbox.lift.hellodarwin.snippet

import _root_.scala.xml.NodeSeq
import _root_.net.liftweb.http.S._
import _root_.net.liftweb.http.SHtml._
import _root_.net.liftweb.http.StatefulSnippet
import _root_.net.liftweb.util.Helpers._
import _root_.net.liftweb.common.Full

class HelloForm4 extends StatefulSnippet{

  val dispatch: DispatchIt = {
    case "show" => show _
  }

  var who = "world"

  def show(xhtml: NodeSeq): NodeSeq = {
    <xml:group>
      Hello {who}
      <br/>
      <label for="whoField">Who :</label>
      { text(who, v => who = v) % ("size" -> "10") % ("id" -> "whoField") }
      { submit(?("Send"), () => println("value:" + who)) }
    </xml:group>
  }
}

