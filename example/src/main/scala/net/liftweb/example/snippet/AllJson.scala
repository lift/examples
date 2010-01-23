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

import net.liftweb._
import http._
import common._
import util._
import js._
import JsCmds._
import JE._

import scala.xml.NodeSeq

// extract a String
object XString {
  def unapply(in: Any): Option[String] = in match {
    case s: String => Some(s)
    case _ => None
  }
}

object XArrayNum {
  def unapply(in: Any): Option[List[Number]] = 
  in match {
    case lst: List[_] => Some(lst.flatMap{case n: Number => Some(n) case _ => None})
    case _ => None
  }
}


object AllJsonHandler extends SessionVar[JsonHandler](
  new JsonHandler {
    def apply(in: Any): JsCmd = in match {
      case JsonCmd("noParam", resp, _, _) =>
        Call(resp)
        
      case JsonCmd("oneString", resp, XString(s), _) =>
        Call(resp, s)
        
      case JsonCmd("addOne", resp, XArrayNum(an), _) =>
        Call(resp, JsArray(an.map(n => Num(n.doubleValue + 1.0)) :_*))
        
      case _ => Noop
    }
  }
)

object AllJson extends DispatchSnippet {
  val dispatch = Map("render" -> buildFuncs _)

  def buildFuncs(in: NodeSeq): NodeSeq =
  Script(AllJsonHandler.is.jsCmd &
         Function("noParam", List("callback"),
                  AllJsonHandler.is.call("noParam",
                                         JsVar("callback"),
                                         JsObj())) &
         Function("oneString", List("callback", "str"),
                  AllJsonHandler.is.call("oneString",
                                         JsVar("callback"),
                                         JsVar("str"))) &
         Function("addOne", List("callback", "nums"),
                  AllJsonHandler.is.call("addOne",
                                         JsVar("callback"),
                                         JsVar("nums"))))

}
}
}
}
