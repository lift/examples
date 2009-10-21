/*
 * FormWithAjax.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.liftweb.example.snippet

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
         "first" -> textAjaxTest(firstName, firstName = _, s => {S.notice("First name "+s); Noop}),
         "last" -> textAjaxTest(lastName, lastName = _, s => {S.notice("Last name "+s); Noop}),
         "submit" -> submit("Send", validate _)
    )
  }
}
