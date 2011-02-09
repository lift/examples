/*
 * Copyright 2007-2010 WorldWide Conferencing, LLC
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

package sandbox.lift {
package hellodarwin {
package snippet{

import _root_.scala.xml.NodeSeq
import _root_.net.liftweb.http.S._
import _root_.net.liftweb.http.SHtml._
import _root_.net.liftweb.util.Helpers._
import _root_.net.liftweb.http.js.{JsCmd, JsCmds, JE}

class HelloFormAjax {
  def whoNode(str: String) = <span id="who">{str}</span>

  def updateWho(str: String): JsCmd = {
    JsCmds.SetHtml("who", whoNode(str))
  }

  def show(xhtml: NodeSeq): NodeSeq = {
    <xml:group>
      Hello {whoNode("world")}
      <br/>
      <label for="whoField">Who :</label>
      { text("world", null) % ("size" -> "10") % ("id" -> "whoField") }
      { <button type="button">{?("Send")}</button> %
       ("onclick" ->
	ajaxCall(JE.JsRaw("$('#whoField').attr('value')"),
		 s => updateWho(s))) }
    </xml:group>
  }
}
}
}
}

