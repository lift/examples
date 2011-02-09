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
package comet {

import scala.xml.{NodeSeq, Text}

import net.liftweb.http._
import net.liftweb.http.js.JsCmds.Noop

import net.liftweb.widgets.flot._
import net.liftweb.common._
import net.liftweb.util._

import net.liftweb.flot_demo.web.model._

/*
 *
 */

class FlotCometExample extends CometActor
{
  var options : FlotOptions = new FlotOptions {}
  var series : List [FlotSerie] = Nil
  val idPlaceholder = "ph_graph"

  override def defaultPrefix = Full("flot")

  def render = {
    bind("flot", "graph" -> Flot.render ("ph_graph", series, options, Noop))
  }

  //

  override def localSetup {
    Sensor.acum !? AddListener(this) match {
      case flotInfo : FlotInfo => {
        options = flotInfo.options
        series = flotInfo.series
      }
    }
  }

  def logFlotSerie = {
    for (serie <- series) {
      println (serie.label)
      println (serie.data)
    }
  }

  override def localShutdown {
    Sensor.acum ! RemoveListener(this)
  }

  override def lowPriority : PartialFunction[Any, Unit] = {
    case newData : FlotNewData => {
      //
      series = newData.series

      val listData = newData.datas
      val js = JsFlotAppendData (idPlaceholder,
                                 series,
                                 listData,
                                 true)

      partialUpdate (js)
    }
  }
}
}
}
}
}
