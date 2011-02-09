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

class SettingOption {

  def render (xhtml: NodeSeq) = {

    def graph () = {

      var d1 : List [Pair [Double, Double]] = Nil
      var d2 : List [Pair [Double, Double]] = Nil
      var d3 : List [Pair [Double, Double]] = Nil

      var i = 0.0
      while (i < Math.Pi * 2.0)
      {
        d1 = (i, Math.sin(i)) :: d1
        d2 = (i, Math.cos(i)) :: d2
        d3 = (i, Math.tan(i)) :: d3

        i += 0.25
      }

      val s1 = new FlotSerie () {
        override val data = d1
        override val label = Full ("sin(x)")
      }

      val s2 = new FlotSerie () {
        override val data = d2
        override val label = Full ("cos(x)")
      }

      val s3 = new FlotSerie () {
        override val data = d3
        override val label = Full ("tan(x)")
      }

      val options = new FlotOptions () {
        override val lines = Full (new FlotLinesOptions () {
          override val show = Full (true)
        })
        override val points = Full (new FlotPointsOptions () {
          override val show = Full (true)
        })
        // TODO x-axis
        override val yaxis = Full (new FlotAxisOptions () {
          override val ticks = 10.0 :: Nil
          override val min = Full (-2.0)
          override val max = Full (2.0)
        })
        override val grid = Full (new FlotGridOptions () {
          override val backgroundColor = Full ("#fffaff")
        })
      }

      Flot.render ( "ph_graph", s3 :: s2 :: s1 :: Nil,  options, Flot.script(xhtml))
    }

    //

    bind ("flot", xhtml, "graph" -> graph)
  }
}
}
}
}
}
