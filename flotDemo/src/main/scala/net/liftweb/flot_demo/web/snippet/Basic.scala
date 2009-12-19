package net.liftweb.flot_demo.web.snippet

import scala.xml.NodeSeq

import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds._
import net.liftweb.widgets.flot._

/*
 *
 */

class Basic {

  def render (xhtml: NodeSeq) = {

    def graph () = {

      var d1 : List [Pair [Double, Double]] = Nil

      for (i <- List.range (0, 140, 5))
        d1 = Pair (i / 10.0, Math.sin(i / 10.0)) :: d1

      val d2 : List [Pair [Double, Double]] = (0.0, 3.0) :: (4.0, 8.0) :: (8.0, 5.0) :: (9.0, 13.0) :: Nil

      var d3 : List [Pair [Double, Double]] = (0.0, 12.0) :: (7.0, 12.0) :: (Math.NaN_DOUBLE, Math.NaN_DOUBLE) ::
                                              (7.0, 2.5) :: (12.0, 2.5) :: Nil

      val s1 = new FlotSerie () {
        override val data = d1
      }

      val s2 = new FlotSerie () {
        override val data = d2
      }

      val s3 = new FlotSerie () {
        override val data = d3
      }

      Flot.render ( "ph_graph", s3 :: s2 :: s1 :: Nil, new FlotOptions {}, Flot.script(xhtml))
    }

    //

    bind ("flot", xhtml, "graph" -> graph)
  }
}
