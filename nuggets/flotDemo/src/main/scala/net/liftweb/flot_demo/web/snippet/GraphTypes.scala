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

class GraphTypes {

  def render (xhtml: NodeSeq) = {

    def graph () = {

      var d1 : List [Pair [Double, Double]] = Nil

      for (i <- List.range (0, 140, 5))
        d1 = Pair (i / 10.0, math.sin(i / 10.0)) :: d1

      val d2 : List [Pair [Double, Double]] = (0.0, 3.0) :: (4.0, 8.0) :: (8.0, 5.0) :: (9.0, 13.0) :: Nil

      var d3 : List [Pair [Double, Double]] = Nil
      for (i <- List.range (0, 140, 5))
        d3 = (i / 10.0, math.cos(i / 10.0)) :: d3

      var d4 : List [Pair [Double, Double]] = Nil
      for (i <- List.range (0, 140, 5))
        d4 = (i / 10.0, math.sqrt(i * 1.0)) :: d4

      var d5 : List [Pair [Double, Double]] = Nil
      for (i <- List.range (0, 140, 5))
        d5 = (i / 10.0, math.sqrt(i / 10.0)) :: d5

      val s1 = new FlotSerie () {
        override val data = d1
        override val lines = Full (new FlotLinesOptions () {
          override val show = Full(true)
          override val fill = Full(true)
        })
      }

      val s2 = new FlotSerie () {
        override val data = d2
        override val bars = Full (new FlotBarsOptions () {
          override val show = Full(true)
        })
      }

      val s3 = new FlotSerie () {
        override val data = d3
        override val points = Full (new FlotPointsOptions () {
          override val show = Full(true)
        })
      }

      val s4 = new FlotSerie () {
        override val data = d4
        override val lines = Full (new FlotLinesOptions () {
          override val show = Full(true)
        })
      }

      val s5 = new FlotSerie () {
        override val data = d5
        override val lines = Full (new FlotLinesOptions () {
          override val show = Full(true)
        })
        override val points = Full (new FlotPointsOptions () {
          override val show = Full(true)
        })
      }

      Flot.render ( "ph_graph", s5 :: s4 :: s3 :: s2 :: s1 :: Nil,
                   new FlotOptions {}, Flot.script(xhtml))
    }

    //

    bind ("flot", xhtml, "graph" -> graph)
  }
}
}
}
}
}
