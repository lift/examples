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

import _root_.net.liftweb.http._
import S._
import js._
import JsCmds._
import JE._
import textile._
import _root_.net.liftweb.common._
import _root_.net.liftweb.util._
import Helpers._
import _root_.scala.xml._

class Json {
  object json extends JsonHandler {
    def apply(in: Any): JsCmd =
    SetHtml("json_result", in match {
        case JsonCmd("show", _, p: String, _) => Text(p)
        case JsonCmd("textile", _, p: String, _) =>
          TextileParser.toHtml(p, Empty)
        case JsonCmd("count", _, p: String, _) => Text(p.length+" Characters")
        case x => <b>Problem... didn't handle JSON message {x}</b>
      })
  }

  def sample(in: NodeSeq): NodeSeq =
  bind("json", in,
       "script" -> Script(json.jsCmd),
       AttrBindParam("onclick",
                     Text(json.call(ElemById("json_select")~>Value,
                                    ElemById("json_question")~>Value).toJsCmd),
                     "onclick"))
}
}
}
}
