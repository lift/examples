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

import _root_.net.liftweb.http._
import _root_.net.liftweb.http.SHtml._

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
class ArcChallenge extends StatefulSnippet {
  var dispatch: DispatchIt = {
    case _ =>
      xhtml =>
        ask
  }

  /**
    * Step 1: Type in a Phrase.
    */
  def ask = {
    <form class="form-inline">
      <div class="form-group">
        <label for="answer">Say Anything:</label>
        {text("",
              p => phrase = p,
              "id" -> "answer",
              "class" -> "form-control mx-sm-3")}
      </div>
      {submit("Submit",
              () => dispatch = {case _ => xhtml => think},
              "class" -> "btn btn-primary")}
    </form>
  }

  /**
    * Step 2: Show a link that takes you to the Phrase you entered.
    */
  def think =
    submit("Click here to see what you said",
           () =>
             dispatch = {
               case _ =>
                 xhtml =>
                   answer
           },
           "class" -> "btn btn-primary")

  /**
    * Step 3: Show the phrase.
    */
  def answer = <p>You said: {phrase}</p>

  private var phrase = ""
}
