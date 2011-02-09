/*
 * Copyright 2010 WorldWide Conferencing, LLC
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
import util._
import Helpers._
import common._

object SessionChecker extends Function2[Map[String, SessionInfo],
                                        SessionInfo => Unit, Unit] with Logger
{
  def defaultKillWhen = 180000L
  // how long do we wait to kill single browsers
  @volatile var killWhen = defaultKillWhen

  @volatile var killCnt = 1

  def apply(sessions: Map[String, SessionInfo],
            destroyer: SessionInfo => Unit): Unit = {
    val cutoff = millis - 180000L
    
    sessions.foreach {
      case (name, si @ SessionInfo(session, agent, _, cnt, lastAccess)) =>
        if (cnt <= killCnt && lastAccess < cutoff) {
          info("Purging "+agent)
          destroyer(si)
        }
    }
  }
}

}
}
}
