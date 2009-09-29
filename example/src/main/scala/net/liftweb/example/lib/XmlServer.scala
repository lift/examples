/*
 * XmlServer.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.liftweb.example.lib

import _root_.net.liftweb._
import http._
import base._
import util._
import Helpers._

object XmlServer {
  def init() {
    LiftRules.dispatch.prepend(NamedPF("Web Services Example") {
        // if the url is "showcities" then return the showCities function
        case Req("showcities":: Nil, _, GetRequest) => XmlServer.showCities

          // if the url is "showstates" "curry" the showStates function with the optional second parameter
        case Req("showstates":: xs, _, GetRequest) =>
          XmlServer.showStates(if (xs.isEmpty) "default" else xs.head)
      })
  }

  def showStates(which: String)(): Box[XmlResponse] =
  Full(XmlResponse(
      <states renderedAt={timeNow.toString}>{
          which match {
            case "red" => <state name="Ohio"/><state name="Texas"/><state name="Colorado"/>

            case "blue" => <state name="New York"/><state name="Pennsylvania"/><state name="Vermont"/>

            case _ => <state name="California"/><state name="Rhode Island"/><state name="Maine"/>
          } }
      </states>))

  def showCities(): Box[XmlResponse] =
  Full(XmlResponse(
      <cities>
        <city name="Boston"/>
        <city name="New York"/>
        <city name="San Francisco"/>
        <city name="Dallas"/>
        <city name="Chicago"/>
      </cities>))
}
