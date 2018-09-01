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

package net.liftweb.example.snippet

import _root_.scala.xml.{NodeSeq, Text, Node}
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.S._
import _root_.net.liftweb.http.SHtml._
import _root_.net.liftweb.util.Helpers._
import _root_.net.liftweb.common._
import _root_.net.liftweb.util._


class CountGame extends StatefulSnippet {
  val dispatch: DispatchIt = {
    case "run" if lastGuess == number => xhtml => win(xhtml)

    case "run" => xhtml => nextGuess(xhtml)

    case "count_down" =>
    xhtml => countDown(attr("from").map(Helpers.toInt).openOr(0))
  }

  def win(xhtml: NodeSeq) =
    ( "#win ^*" #> "#choose" &
      ".number" #> Text(number.toString) &
      ".count" #> Text(count.toString)
      ).apply(xhtml) ++
      <p>Counting backward:
        {countDown(number)}
      </p>

  def countDown(number: Int): Node = if (number <= 0) Text("")
  else <xml:group>{number} <lift:count_game.count_down from={(number - 1).toString}/></xml:group>

  def nextGuess(xhtml: NodeSeq): NodeSeq =
    ("#guess ^*" #> "#choose" &
     ".input" #> text("", guess _, "class" -> "form-control") &
      ".last *" #> lastGuess.map(v =>
        if (v < number) v + " is low"
        else v + " is high").
        openOr("Make first Guess")
      ).apply(xhtml)

  private def guess(in: String) {
    count += 1
    lastGuess = Full(toInt(in))
  }

  private val number = 1 + randomInt(100)
  private var lastGuess: Box[Int] = Empty
  private var count = 0
}

