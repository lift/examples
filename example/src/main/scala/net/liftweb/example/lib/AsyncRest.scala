/*
 * Copyright 2011 WorldWide Conferencing, LLC
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

package net.liftweb 
package example 
package lib 

import net.liftweb._
import http._
import common._
import rest._

/**
 * An example of Lift's RestHelper and RestContinuation
 */
object AsyncRest extends RestHelper {

  // serve the URL /async/:id
  serve {
    case "async" :: id :: _ Get _ => 

      // move the calculation to another thread
      RestContinuation.async(
        reply => {
          Thread.sleep(2000) // sleep for 2 seconds
          val name1 = Thread.currentThread.getName
          val outerSesStr = S.session.toString // this should be Empty
          
          // the code block for reply will be executed in the
          // scope of the original request and that may mean
          // that JDBC connections are consumed, etc.
          reply{
            val name2 = Thread.currentThread.getName
            val innerSesStr = S.session.toString // this should be Full()
            <i id={id}>name1: {name1} outer: {outerSesStr} name2: {name2}
            inner: {innerSesStr}</i>
          }
        })
  }
}


