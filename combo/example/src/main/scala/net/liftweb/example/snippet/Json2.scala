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

package net.liftweb.example.snippet

import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml
import net.liftweb.http.js.{JE, JsCmd, JsonCall}
import net.liftweb.common.Logger
import net.liftweb.json.JsonAST._
import net.liftweb.http.js.JsCmds.Alert
import net.liftweb.json.DefaultFormats

// Example taken from http://cookbook.liftweb.net/book.html#id-rjCJTYFwuYtbh9
object JsonCall {

  private val logger = Logger(classOf[JsonCall])
  implicit val formats = DefaultFormats

  case class Question(first: Int, second: Int, answer: Int) {
    def valid_? = first + second == answer
  }

  def render = {

    def validate(value: JValue): JsCmd = {
      logger.info(value)
      value.extractOpt[Question].map(_.valid_?) match {
        case Some(true)  => Alert("Looks good")
        case Some(false) => Alert("That doesn't add up")
        case None        => Alert("That doesn't make sense")
      }
    }

    "button [onclick]" #>
      SHtml.jsonCall(JE.Call("currentQuestion"), validate _)
  }
}
