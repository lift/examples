package net.liftweb.flot_demo.web.snippet

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
