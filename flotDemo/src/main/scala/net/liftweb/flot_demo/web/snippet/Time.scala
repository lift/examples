package net.liftweb.flot_demo.web.snippet

import scala.xml.NodeSeq

import net.liftweb.util._
import Helpers._
import net.liftweb.http.js.JsCmds._

import net.liftweb.widgets.flot._

/*
 *
 */

class Time {

  def render (xhtml: NodeSeq) = {

    def graph () = {

      var d1a : List [Pair [Double, Double]] =
         (-373597200000.0, 315.71) ::  (-370918800000.0, 317.45) ::  Nil

      var d1b : List [Pair [Double, Double]] =
         (-247366800000.0, 319.69) ::  (-244688400000.0, 320.58) :: Nil

      var d2a : List [Pair [Double, Double]] =
         (-123555600000.0, 321.59) ::  (-121136400000.0, 322.39) :: Nil

      var d2b : List [Pair [Double, Double]] =
         (-2682000000.0, 324.12) ::  (-3600000.0, 325.06) :: Nil

      var d3a : List [Pair [Double, Double]] =
         (113007600000.0, 329.31) ::  (115686000000.0, 327.51) :: Nil

      var d3b : List [Pair [Double, Double]] =
         (236559600000.0, 334.73) ::  (239238000000.0, 332.52) :: Nil

      var d4a : List [Pair [Double, Double]] =
         (352249200000.0, 341.57) ::  (354924000000.0, 342.56) :: Nil


      var d4b : List [Pair [Double, Double]] =
         (478479600000.0, 347.49) ::  (481154400000.0, 348.00) :: Nil

      var d5a : List [Pair [Double, Double]] =
         (594342000000.0, 350.04) ::  (596934000000.0, 351.29) :: Nil

      var d5b : List [Pair [Double, Double]] =
         (715298400000.0, 352.79) ::  (717894000000.0, 353.20) :: Nil

      var d6a : List [Pair [Double, Double]] =
         (830901600000.0, 364.94) ::  (833580000000.0, 364.70) :: Nil

      var d6b : List [Pair [Double, Double]] =
         (951865200000.0, 370.38) ::  (954540000000.0, 371.63) :: Nil

      var d7a : List [Pair [Double, Double]] =
         (1067641200000.0, 374.77) ::  (1070233200000.0, 375.97) :: Nil

      val s = new FlotSerie () {
        override val data = d1a ::: d1b ::: d2a ::: d2b ::: d3a ::: d3b ::: d4a ::: d4b :::
                            d5a ::: d5b ::: d6a ::: d6b ::: d7a
      }

      val options = new FlotOptions () {
        override val xaxis = Full (new FlotAxisOptions () {
          override val mode = Full ("time")
        })
      }

      Flot.render ( "ph_graph", s :: Nil, options, Flot.script(xhtml))
    }

    //

    bind ("flot", xhtml, "graph" -> graph)
  }
}
