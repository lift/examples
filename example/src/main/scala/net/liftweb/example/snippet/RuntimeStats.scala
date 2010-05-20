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
import common._
import util._
import Helpers._

import _root_.java.text.NumberFormat
import _root_.scala.xml.{NodeSeq, Text}

object RuntimeStats extends DispatchSnippet {
  @volatile
  var totalMem: Long = Runtime.getRuntime.totalMemory
  @volatile
  var freeMem: Long = Runtime.getRuntime.freeMemory

  @volatile
  var sessions = 1

  @volatile
  var lastUpdate = timeNow

  val startedAt = timeNow

  private def nf(in: Long): String = NumberFormat.getInstance.format(in)

  def dispatch = {
    case "total_mem" => i => Text(nf(totalMem))
    case "free_mem" => i => Text(nf(freeMem))
    case "sessions" => i => Text(sessions.toString)
    case "updated_at" => i => Text(lastUpdate.toString)
    case "started_at" => i => Text(startedAt.toString)
  }

}

}
}
}
