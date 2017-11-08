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

import _root_.net.liftweb.example.model._
import _root_.scala.xml.{NodeSeq, Text, Group, Node}
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.S
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.http.S._
import _root_.net.liftweb.http.SHtml._
import _root_.net.liftweb.util.Helpers._
import _root_.net.liftweb.common._
import _root_.net.liftweb.util._
import _root_.scala.collection.mutable.HashMap


/**
  * This session variable holds a set of Names and Integers, with a default value
  * of 0 (zero)
  */
object CountHolder extends SessionVar[HashMap[String, Int]](new HashMap[String, Int] {
  override def default(key: String): Int = 0
})

/**
  * This snippet handles counting
  */
class Count {

  /**
    * This method is invoked by the &lt;lift:Count /&gt; tag
    */
  def render(in: NodeSeq): NodeSeq = {
    // get the attribute name (the name of the thing to count)
    val attr: String = S.attr("name").openOr("N/A")

    // Get the value from the session variable
    val value = CountHolder.is(attr)

    // Bind the incoming view data and the model data
    bind("count", in, "value" -> value,
    "incr" -> link("/count", () => CountHolder.is(attr) = value + 1, Text("++")),
    "decr" -> link("/count", () => CountHolder.is(attr) = 0 max (value - 1), Text("--")))
  }
}
}
}
}
