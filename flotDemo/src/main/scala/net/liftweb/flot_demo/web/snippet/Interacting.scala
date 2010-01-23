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
package flot_demo {
package web {
package snippet {

import scala.xml.NodeSeq

import net.liftweb.common._
import net.liftweb.util._
import Helpers._
import net.liftweb.http.js.JsCmds._
import net.liftweb.widgets.flot._

/*
 *
 */

class Interacting {

  def render (xhtml: NodeSeq) = {

    def graph () = {

      var d : List [Pair [Double, Double]] = Nil

      for (i <- List.range (0, 140, 5))
        d = (i / 10.0, Math.sin(i / 10.0)) :: d

      val s = new FlotSerie () {
        override val data = d
      }

      val options = new FlotOptions () {
        override val grid = Full (new FlotGridOptions () {
          override val clickable = Full (true)
        })
      }

      Flot.render ( "ph_graph", s :: Nil, options, Flot.script(xhtml))
    }

    //

    bind ("flot", xhtml, "graph" -> graph)
  }
}
}
}
}
}
