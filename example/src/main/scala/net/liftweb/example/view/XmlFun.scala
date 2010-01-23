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
package example {
package view {

import _root_.scala.xml.{Text, Node, NodeSeq}
import _root_.net.liftweb.http._
import S._
import _root_.net.liftweb.common._
import _root_.net.liftweb.util._

class XmlFun extends LiftView {
  def dispatch = Map("index" -> render _)

  def render = {
    val addresses = List(
      addressNode("123 any street", null, "SF", "CA", "94122", "US"),
        addressNode("456 other lane", "flat 3", "London", "", "NW3", "GB"),
        addressNode("14 gordon st", "#204", "Brighton", "MA", "02135", "US"),
        addressNode("37 foo lane", null, "Ixtapa", "MX", "ABC", "MX"),
        addressNode("44 sheep st", "#1", "Liverpool", "", "GE1", "GB"),
        addressNode("74 nice st", "#1801", "Chicago", "IL", "60606", "US"))

    val toCount = param("country") openOr "US"

    Full(
      <lift:surround with="default" at="content">

        <p>The XML is
          <pre>{addresses.map{e => Text(e.toString) :: <br/> :: Nil}}
          </pre></p>
        <p>The count for {toCount} nodes is {countryCount(toCount, addresses)}</p>

        <p><a href='/xml_fun'>Count US addresses.</a></p>
        <p><a href='/xml_fun?country=GB'>Count GB addresses.</a></p>


      </lift:surround>)
  }

  private def addressNode(line1: String, line2: String, city: String,
                          state: String, zip_pc: String, country: String) =
  <address>
    <line>{line1}</line>{
      if (line2 != null && line2.length > 0) <line>{line2}</line> else Text("")}
    <city>{city}</city>  <state>{state}</state> <country>{country}</country>
  </address>


  private def countryCount(toMatch: String, xml: NodeSeq) =
  (for {
      addr <- xml \\ "address"
      country <- addr \\ "country" if country.text == toMatch}
   yield country).length

}
}
}
}
