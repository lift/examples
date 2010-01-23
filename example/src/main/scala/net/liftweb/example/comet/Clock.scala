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
package comet {

import _root_.net.liftweb._
import http._
import common._
import util._
import Helpers._
import js._
import JsCmds._
import _root_.scala.xml.Text

class Clock extends CometActor {
  override def defaultPrefix = Full("clk")
  // schedule a ping every 10 seconds so we redraw
  ActorPing.schedule(this, Tick, 10 seconds)

  private lazy val spanId = uniqueId+"_timespan"

  def render = {
    bind("time" -> timeSpan)
  }

  def timeSpan = (<span id={spanId}>{timeNow}</span>)

  override def lowPriority = {
    case Tick =>
      partialUpdate(SetHtml(spanId, Text(timeNow.toString)))
      ActorPing.schedule(this, Tick, 10 seconds)
  }
}

case object Tick
}
}
}
