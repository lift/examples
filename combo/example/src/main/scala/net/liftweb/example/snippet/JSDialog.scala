/*
 * Copyright 2009-2010 WorldWide Conferencing, LLC
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

package net.liftweb {
package example {
package snippet {

import _root_.net.liftweb._
import http._
import SHtml._
import js._
import JsCmds._
import js.jquery._
import JqJsCmds._
import common._
import util._
import Helpers._

import _root_.scala.xml.NodeSeq

class JSDialog {
  // build the button... when pressed, present
  // a dialog based on running the _jsdialog_confirm
  // template
  def button(in: NodeSeq) =
  ajaxButton(in,
             () => S.runTemplate(List("_jsdialog_confirm")).
             map(ns => ModalDialog(ns)) openOr
             Alert("Couldn't find _jsdialog_confirm template"))

  // the template needs to bind to either server-side behavior
  // and unblock the UI
  def confirm(in: NodeSeq) =
  bind("confirm", in,
       "yes" -> ((b: NodeSeq) => ajaxButton(b, () =>
        {println("Rhode Island Destroyed")
         Unblock & Alert("Rhode Island Destroyed")})),
       "no" -> ((b: NodeSeq) => <button onclick={Unblock.toJsCmd}>{b}</button>))
}
}
}
}
