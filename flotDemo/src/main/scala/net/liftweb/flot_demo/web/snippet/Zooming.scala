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

class Zooming {

  def getData (x1 : Double, x2 : Double) : List [Pair [Double, Double]] = {
    var ret : List [Pair [Double, Double]] = Nil

    var i = x1
    while (i < x2) {
      ret = (i, Math.sin (i * Math.sin(i))) :: ret
      i += (x2 - x1) / 100
    }

    ret
  }

  def render (xhtml: NodeSeq) = {

    /*
    */

    def graph () = {
      val data = new FlotSerie () {
        override val label = Full ("sin(x sin(x))")
        override val data = getData (0.0, 3 * Math.Pi)
      }

      val options = new FlotOptions () {
        override val lines = Full(new FlotLinesOptions () {
          override val show = Full (true)
        })
        override val points = Full(new FlotPointsOptions () {
          override val show = Full (true)
        })
        override val yaxis = Full (new FlotAxisOptions () {
          override val ticks = 10.0 :: Nil
        })
        override val legend = Full (new FlotLegendOptions () {
          override val show = Full (false)
        })
        override val modeSelection = Full ("xy")
      }

      val optionsOverview = new FlotOptions () {
        override val lines = Full(new FlotLinesOptions () {
          override val show = Full (true)
          override val lineWidth = Full (1)
        })
        override val legend = Full (new FlotLegendOptions () {
          override val show = Full (true)
          override val container = Full ("ph_legend")
        })
        override val grid = Full (new FlotGridOptions () {
          override val color = Full ("#999")
        })
        override val shadowSize = Full (0)
        override val xaxis = Full (new FlotAxisOptions () {
          override val ticks = 4.0 :: Nil
        })
        override val yaxis = Full (new FlotAxisOptions () {
          override val ticks = 3.0 :: Nil
          override val min = Full (-2.0)
          override val max = Full (2.0)
        })
        override val modeSelection = Full ("xy")
      }

      val overview = new FlotOverview ("ph_overview", optionsOverview)

      Flot.render ("ph_graph", data :: Nil, options, Flot.script(xhtml), overview)
    }

    //

    bind ("flot", xhtml, "graph" -> graph)
  }
}
}
}
}
}
