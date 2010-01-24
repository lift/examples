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

package net.liftweb {
package example {
package snippet {

import _root_.net.liftweb._
import http._
import SHtml._
import js._
import JsCmds._
import common._
import util._
import Helpers._

import scala.xml.NodeSeq

class FormWithAjax extends StatefulSnippet {
  private var firstName = ""
  private var lastName = ""
  private val from = S.referer openOr "/"

  def dispatch = {
    case _ => render _
  }

  def render(xhtml: NodeSeq): NodeSeq =
  {


    def validate() {
      (firstName.length, lastName.length) match {
        case (f, n) if f < 2 && n < 2 => S.error("First and last names too short")
        case (f, _) if f < 2 => S.error("First name too short")
        case (_, n) if n < 2 => S.error("Last name too short")
        case _ => S.notice("Thanks!"); S.redirectTo(from)
      }
    }

    bind("form", xhtml,
         "first" -> textAjaxTest(firstName, s => firstName = s, s => {S.notice("First name "+s); Noop}),
         "last" -> textAjaxTest(lastName, s => lastName = s, s => {S.notice("Last name "+s); Noop}),
         "submit" -> submit("Send", validate _)
    )
  }
}
}
}
}
