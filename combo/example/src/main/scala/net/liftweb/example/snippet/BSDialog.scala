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

package net.liftweb.example.snippet

import net.liftweb.http.SHtml._
import net.liftweb.http._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js._
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.util.Helpers._
import net.liftweb.common._
import scala.xml.NodeSeq

class BSDialog {
  // build the button... when pressed, present
  // a dialog based on the _bsdialog_confirm
  // template
  def button(in: NodeSeq) =
    ajaxButton(in,
               () => injectDialogTemplate & openDialog,
               "type" -> "button",
               "class" -> "btn btn-primary",
               "data-target" -> "#exampleModal")

  def confirm = {
    "#yes" #> ((b: NodeSeq) =>
      ajaxButton(b, () => {
        logger.debug("Rhode Island Destroyed")
        showDestroyAlert & closeDialog
      }, "type" -> "button", "class" -> "btn btn-primary")) &
      "#no" #> ((b: NodeSeq) =>
        ajaxButton(
          b,
          () => {
            logger.debug("Rhode Island intact")
            Noop //we could have used closeDialog here but we are instead using the data-dismiss attribute.
          },
          "type" -> "button",
          "class" -> "btn btn-primary",
          "data-dismiss" -> "modal"
        ))
  }

  private val logger = Logger(classOf[BSDialog])
  private val bsDialogTemplate: Box[NodeSeq] = Templates(
    List("_bsdialog_confirm"))
  private val openDialog
    : JsCmd = JsRaw("""$('#exampleModal').modal('show')""").cmd
  private val closeDialog
    : JsCmd = JsRaw("""$('#exampleModal').modal('hide')""").cmd
  private val showDestroyAlert = Alert("Rhode Island Destroyed")
  private val showTemplateNotFoundAlert = Alert(
    "Couldn't find _bsdialog_confirm template")

  private def setAtPlaceholder(ns: NodeSeq) = SetHtml("modalPlaceholder", ns)
  private def injectDialogTemplate =
    bsDialogTemplate map setAtPlaceholder openOr showTemplateNotFoundAlert
}
