package net.liftweb.flot_demo.web.snippet

import scala.xml.NodeSeq

import net.liftweb.base._
import net.liftweb.util._
import Helpers._
import net.liftweb.http.js.JsCmds._

import net.liftweb.widgets.flot._

/*
 *
 */

class Selection {

  def render (xhtml: NodeSeq) = {

    /*
    */

    def graph () = {
      val us = new FlotSerie () {
        override val color = Full (Right(0))
        override val label = Full ("United States")
        override val data = (1990.0, 18.9) ::  (1991.0, 18.7) ::  (1992.0, 18.4) ::
                            (1993.0, 19.3) ::  (1994.0, 19.5) ::  (1995.0, 19.3) ::
                            (1996.0, 19.4) ::  (1997.0, 20.2) ::  (1998.0, 19.8) ::
                            (1999.0, 19.9) ::  (2000.0, 20.4) ::  (2001.0, 20.1) ::
                            (2002.0, 20.0) ::  (2003.0, 19.8) ::  (2004.0, 20.4) :: Nil
      }

      val russia = new FlotSerie () {
        override val color = Full (Right(1))
        override val label = Full ("Russia")
        override val data =  (1992.0, 13.4) ::  (1993.0, 12.2) ::  (1994.0, 10.6) ::
                             (1995.0, 10.2) ::  (1996.0, 10.1) ::  (1997.0, 9.7) ::
                             (1998.0, 9.5) ::  (1999.0, 9.7) ::  (2000.0, 9.9) ::
                             (2001.0, 9.9) ::  (2002.0, 9.9) ::  (2003.0, 10.3) ::
                             (2004.0, 10.5) :: Nil
      }

      val uk = new FlotSerie () {
        override val color = Full (Right(2))
        override val label = Full ("United Kingdom")
        override val data =  (1990.0, 10.0) ::  (1991.0, 11.3) ::  (1992.0, 9.9) ::
                             (1993.0, 9.6) ::  (1994.0, 9.5) ::  (1995.0, 9.5) ::
                             (1996.0, 9.9) ::  (1997.0, 9.3) ::  (1998.0, 9.2) ::
                             (1999.0, 9.2) ::  (2000.0, 9.5) ::  (2001.0, 9.6) ::
                             (2002.0, 9.3) ::  (2003.0, 9.4) ::  (2004.0, 9.79) :: Nil
      }

      val de = new FlotSerie () {
        override val color = Full (Right(3))
        override val label = Full ("Germany")
        override val data =  (1990.0, 12.4) ::  (1991.0, 11.2) ::  (1992.0, 10.8) ::
                             (1993.0, 10.5) ::  (1994.0, 10.4) ::  (1995.0, 10.2) ::
                             (1996.0, 10.5) ::  (1997.0, 10.2) ::  (1998.0, 10.1) ::
                             (1999.0, 9.6) ::  (2000.0, 9.7) ::  (2001.0, 10.0) ::
                             (2002.0, 9.7) ::  (2003.0, 9.8) ::  (2004.0, 9.79) :: Nil
      }

      val dk = new FlotSerie () {
        override val color = Full (Right(4))
        override val label = Full ("Denmark")
        override val data =  (1990.0, 9.7) ::  (1991.0, 12.1) ::  (1992.0, 10.3) ::
                             (1993.0, 11.3) ::  (1994.0, 11.7) ::  (1995.0, 10.6) ::
                             (1996.0, 12.8) ::  (1997.0, 10.8) ::  (1998.0, 10.3) ::
                             (1999.0, 9.4) ::  (2000.0, 8.7) ::  (2001.0, 9.0) ::
                             (2002.0, 8.9) ::  (2003.0, 10.1) ::  (2004.0, 9.80) :: Nil
      }

      val sw = new FlotSerie () {
        override val color = Full (Right(5))
        override val label = Full ("Sweden")
        override val data =  (1990.0, 5.8) ::  (1991.0, 6.0) ::  (1992.0, 5.9) ::
                             (1993.0, 5.5) ::  (1994.0, 5.7) ::  (1995.0, 5.3) ::
                             (1996.0, 6.1) ::  (1997.0, 5.4) ::  (1998.0, 5.4) ::
                             (1999.0, 5.1) ::  (2000.0, 5.2) ::  (2001.0, 5.4) ::
                             (2002.0, 6.2) ::  (2003.0, 5.9) ::  (2004.0, 5.89) :: Nil
      }

      val nw = new FlotSerie () {
        override val color = Full (Right(6))
        override val label = Full ("Norway")
        override val data =  (1990.0, 8.3) ::  (1991.0, 8.3) ::  (1992.0, 7.8) ::
                             (1993.0, 8.3) ::  (1994.0, 8.4) ::  (1995.0, 5.9) ::
                             (1996.0, 6.4) ::  (1997.0, 6.7) ::  (1998.0, 6.9) ::
                             (1999.0, 7.6) ::  (2000.0, 7.4) ::  (2001.0, 8.1) ::
                             (2002.0, 12.5) ::  (2003.0, 9.9) ::  (2004.0, 19.0) :: Nil
      }

      val options = new FlotOptions () {
        override val lines = Full(new FlotLinesOptions () {
          override val show = Full (true)
        })
        override val points = Full(new FlotPointsOptions () {
          override val show = Full (true)
        })
        override val xaxis = Full (new FlotAxisOptions () {
          override val tickDecimals = Full (0.0)
        })
        override val yaxis = Full (new FlotAxisOptions () {
          override val min = Full (0.0)
        })
        override val legend = Full (new FlotLegendOptions () {
          override val noColumns = Full (4)
          override val container = Full ("ph_legend")
        })
        override val modeSelection = Full ("x")
      }

      Flot.render ( "ph_graph", us :: russia :: uk :: de ::
                   dk :: sw :: nw :: Nil, options, Flot.script(xhtml))
    }

    //

    bind ("flot", xhtml, "graph" -> graph)
  }
}
