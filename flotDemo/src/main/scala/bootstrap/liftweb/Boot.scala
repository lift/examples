package bootstrap.liftweb

import net.liftweb.base._
import net.liftweb.util._
import net.liftweb.http._
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._
import Helpers._

import _root_.net.liftweb._
import http._
import sitemap._
import Helpers._

/**
  */
class Boot {
  def boot {
    LiftRules.addToPackages("net.liftweb.flot_demo.web")

    // Build SiteMap
    val entries = Menu(Loc("Home", List ("index"), "Home")) ::
                  Menu(Loc("Flot: Basic", List ("basic"), "Flot: Basic example")) ::
                  Menu(Loc("Flot: Graph-Types", List ("graph-types"), "Flot: Different graph types")) ::
                  Menu(Loc("Flot: Setting-Option", List ("setting-option"), "Flot: Setting various options")) ::
                  Menu(Loc("Flot: Selection", List ("selection"), "Flot: Selection and zooming")) ::
                  Menu(Loc("Flot: Zooming", List ("zooming"), "Flot: Zooming with overview")) ::
                  Menu(Loc("Flot: Time", List ("time"), "Flot: Plotting times series")) ::
                  Menu(Loc("Flot: Visitors", List ("visitors"), "Flot: Visitors per day")) ::
                  Menu(Loc("Flot: Interacting", List ("interacting"), "Flot: Interacting with the data")) ::
                  Menu(Loc("flot+comet", List ("flot-comet"), "Flot+Comet")) ::
                  Nil

    LiftRules.setSiteMap(SiteMap(entries:_*))

    // register treetable resources (javascript and gifs)
    net.liftweb.widgets.flot.Flot.init ()

    // used to test the comet actor
    net.liftweb.flot_demo.web.model.Sensor.start
  }
}
