/*
 * Copyright 2010 WorldWide Conferencing, LLC
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

import net.liftweb._
import http._
import util._
import Helpers._
import js.jquery._
import scala.xml.NodeSeq

/**
  * A simple example of Wiring.
  * The count of done To-do items
  */
class SimpleWiring {
  // define the cells
  private val feedFish = ValueCell(false)
  private val walkDog = ValueCell(false)
  private val doDishes = ValueCell(false)
  private val watchTv = ValueCell(false)

  // Our count is the collection of cells and we sum them up
  private val count = SeqCell(feedFish, walkDog, doDishes, watchTv).lift {
    _.map(_.toInt).sum
  }

  private class BtoI(b: Boolean) { def toInt: Int = if (b) 1 else 0 }
  private implicit def bToI(b: Boolean): BtoI = new BtoI(b)

  // define the count transformation
  def count(in: NodeSeq): NodeSeq =
    WiringUI.asText(in, count, JqWiringSupport.fade)

  def toDo = {
    import SHtml._
    "* *" #> List[NodeSeq](
      <div class="form-check">
        {ajaxCheckboxElem(feedFish)}
        <label class="form-check-label">Feed Fish</label>
      </div>,
      <div class="form-check">
        {ajaxCheckboxElem(walkDog)}
        <label class="form-check-label">Walk Dog</label>
      </div>,
      <div class="form-check">
        {ajaxCheckboxElem(doDishes)}
        <label class="form-check-label">Do Dishes</label>
      </div>,
      <div class="form-check">
        {ajaxCheckboxElem(watchTv)}
        <label class="form-check-label">Watch TV</label>
      </div>
    )
  }

}
