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

package net.liftweb {
package example {
package lib {

import _root_.net.liftweb._
import http._
import common._

import scala.xml._

/**
 * Use Lift's templating without a session and without state
 */
object StatelessHtml {
  private val fakeSession = new LiftSession("/", "fakeSession", Empty)

  def render(req: Req)(): Box[LiftResponse] = {
    val xml: Box[NodeSeq] = S.init(req, fakeSession) {
      S.runTemplate(List("stateless"))
    }
    xml.map(ns => XhtmlResponse(ns(0), Empty, Nil, Nil, 200, false))
  }
}

}
}
}
