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
import S._
import common._
import util._
import Helpers._

import model._

import _root_.scala.xml.{NodeSeq, Text, Group}

object Template extends DispatchSnippet {
  def dispatch: DispatchIt =
  Map("show" -> show _)

  def show(in: NodeSeq): NodeSeq = {
    val ret: Box[NodeSeq] =
    for (tmpl <- templateFromTemplateAttr;
         (tbl, row) <- template(tmpl, "temp", "tbl", "row"))
    yield {
      val rows: NodeSeq =
      User.findAll match {
        case Nil => bind("item", row, "one" -> "No Records Found",
                         "two" -> "")
        case xs => xs.flatMap(u => bind("item", row,
                                        "one" -> u.firstName.is,
                                        "two" -> u.email.is))
      }

      bind("head", tbl, "one" -> "Name",
           "two" -> "Email",
           "rows" -> rows)
    }

    ret match {
      case Full(xs) => xs
      case Empty => Text("Error processing template")
      case Failure(msg, _, _) => Text("Error processing template: "+msg)
    }

  }

}

}
}
}
