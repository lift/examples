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
package model {

import scala.collection.mutable.{HashMap, HashSet}

import net.liftweb.widgets.flot._
import net.liftweb.common._
import net.liftweb.util._
import net.liftweb.actor._

/*
 * This examples simulates a sampling device that takes every 2 seconds different measures.
 * It could be, for example, a meteological station that measures the wind speed, the temperature,
 * and rain falls.
 */

/**
 * in each sample, we can have diferent measures
 */

case class Sample (time: Long, measures: List[Double]) {
  override def toString () = {
    "time: " + time +
      ", values: " + measures.foldLeft ("") ((sz, value) => sz + " " + value)
  }
}

//

case class AddListener(listener: LiftActor)

case class RemoveListener(listener: LiftActor)

/**
 * can a "window" of samples in memory
 */

class AcumSamplesActor (max : Int) extends LiftActor {

  val options = new FlotOptions () {
        override val xaxis = Full (new FlotAxisOptions () {
          override val mode = Full ("time")
        })
      }

  var series : List [FlotSerie] =
    new FlotSerie () {
      override val label = Full ("Serie 1")
      override val data = Nil
    } ::
    new FlotSerie () {
      override val label = Full ("Serie 2")
      override val data = Nil
    } ::
    new FlotSerie () {
      override val label = Full ("Serie 3")
      override val data = Nil
    } ::
    Nil

  // listeners
  var listeners: List[LiftActor] = Nil

  def notifyListeners (newData : FlotNewData) = {
    listeners.foreach(_ ! newData)
  }


  protected def messageHandler = {
        // nueva Sample
    case sample : Sample => {
      
      // actualiza series flot
      val seq = for (z <- series zip sample.measures) yield {
        new FlotSerie () {
            override val label = z._1.label
          override val data = z._1.data.takeRight (max) ::: List ((0.0 + sample.time, z._2))
        }
	}
      
      series = seq.toList
      
	val newDatas = (for (medida <- sample.measures) yield (0.0 + sample.time, medida)).toList
      
      // send the new sampling values to the listener
	notifyListeners (FlotNewData (series, newDatas))
    }
    
    case AddListener(listener: LiftActor) =>
      listeners = listener :: listeners
    //
    reply (FlotInfo ("", series, options))
    
    
      case RemoveListener(listener: LiftActor) =>
        listeners = listeners.remove(listener.eq)
  }
}

// simulates a sensor sending every 2 seconds a sample with 3 measurements:

object Sensor extends _root_.java.lang.Runnable {
  val acum = new AcumSamplesActor (10)

  def start () = {
    new Thread (this).start
  }

  override def run () : Unit = {
    while (true)
    {
      val time = new _root_.java.util.Date ().getTime ()

      val sinus = Math.sin (time)
      val cosinus = Math.cos (time)
      val both = sinus + 2.0 * cosinus

      acum ! Sample (time, List (sinus, cosinus, both))

      Thread.sleep (2000)
    }
  }
}
}
}
}
}
