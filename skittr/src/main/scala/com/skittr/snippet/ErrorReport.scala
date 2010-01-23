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

package com.skittr {
package model {

import _root_.net.liftweb.http._
import _root_.scala.xml._

class ErrorReport {
  def render(styles: Group): NodeSeq =
    List((S.errors, (styles \\ "error_msg"), "Error", (styles \\ "error_class")),
        (S.warnings, (styles \\ "warning_msg"), "Warning", (styles \\ "warning_class")),
        (S.notices, (styles \\ "notice_msg"), "Notice", (styles \\ "notice_class"))).flatMap {
      v =>
      val msg = v._1
      val title = v._2.filter(_.prefix == "lift").take(1).text match {
        case s if (s.length > 0) => s
        case _ => v._3
      }

      val style = v._4.filter(_.prefix == "lift").take(1).text
      if (msg.isEmpty) Nil
      else {
        val msgList = msg.flatMap(e => <li>{e._1}</li>)
        if (style != "") <div class={style}>{title}:<ul>{msgList}</ul></div>
        else <h1>{title}</h1>++{msgList}
      }
    }

}
}
}
