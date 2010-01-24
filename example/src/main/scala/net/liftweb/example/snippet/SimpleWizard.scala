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
package snippet {

import _root_.net.liftweb.example.model._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.S
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.http.S._
import _root_.net.liftweb.http.SHtml._
import _root_.net.liftweb.util.Helpers._
import _root_.net.liftweb.common._
import _root_.net.liftweb.util._
import _root_.scala.xml.{NodeSeq, Text, Group}

/**
 * The Arc Challenge is Paul Graham's quest for web framework concision.
 *
 * http://www.paulgraham.com/arcchallenge.html
 *
 * This is one potential lift-based solution to it using StatefulSnippets.
 * There are doubtless many other ways.
 *
 * @author: Steve Jenson
 */
class SimpleWizard extends StatefulSnippet {
  val fromWhence = S.referer openOr "/"
  var dispatch: DispatchIt = {case _ => xhtml => pageOne}
  var name = ""
  var quest = ""
  var color = ""

  /**
   * pageOne -- Ask the name
   */
  def pageOne = {
  def validate() {
    this.registerThisSnippet()
    if (name.length < 2) S.error(S.?("Name too short"))
    else dispatch = {case _ => xhtml => pageTwo}
  }

  TemplateFinder.findAnyTemplate(List("templating", "pageOne")).map(html =>
  bind("wizard", html, "name" -> text(name, s => name = s), "submit" -> submit(S.?("Next"), validate))) openOr NodeSeq.Empty
  }

  /**
   * pageTwo -- Ask the quest
   */
  def pageTwo = {
  def validate() {
    this.registerThisSnippet()
    if (quest.length < 2) S.error(S.?("Quest too short"))
    else dispatch = {case _ => xhtml => pageThree}
  }

  TemplateFinder.findAnyTemplate(List("templating", "pageTwo")).map(html =>
  bind("wizard", html, "quest" -> text(quest, s => quest = s), "submit" -> submit(S.?("Next"), validate))) openOr NodeSeq.Empty
  }

    /**
   * pageThree -- Ask the color
   */
  def pageThree = {
  def validate() {
     this.registerThisSnippet()
    if (!List("red", "yellow", "blue").contains(color.toLowerCase)) S.error(S.?("Color not red, yellow or blue"))
    else {
      S.notice("You, "+name+" on the quest "+quest+" may cross the bridge of sorrows")
      S.redirectTo(fromWhence)
    }
  }

  TemplateFinder.findAnyTemplate(List("templating", "pageThree")).map(html =>
  bind("wizard", html, "color" -> text(color, s => color = s), "submit" -> submit(S.?("Finish"), validate))) openOr NodeSeq.Empty
  }

}
}
}
}
