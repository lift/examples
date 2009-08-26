package com.skittr.snippet


/*                                                *\
 (c) 2007 WorldWide Conferencing, LLC
 Distributed under an Apache License
 http://www.apache.org/licenses/LICENSE-2.0
\*                                                 */

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
