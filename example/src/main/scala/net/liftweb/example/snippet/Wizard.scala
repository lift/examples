/*
 * Copyright 2009-2010 WorldWide Conferencing, LLC
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

import _root_.scala.xml.{NodeSeq, Text}
import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.wizard._
import _root_.net.liftweb.common._
import _root_.java.util.Date
import Helpers._

import model._

/**
 * An example of a wizard in Lift
 */
object MyWizard extends Wizard {
  object completeInfo extends WizardVar(false)

  // define the first screen
  val nameAndAge = new Screen {

    // it has a name field
    val name = field(S ? "First Name", "",
                     valMinLen(2, S ? "Name Too Short"),
                     valMaxLen(40, S ? "Name Too Long"))

    // and an age field
    val age = field(S ? "Age", 0, minVal(5, S ?? "Too young"),
      maxVal(120, S ? "You should be dead"))

    // choose the next screen based on the age
    override def nextScreen = if (age.is < 18) parentName else favoritePet
  }

  // We ask the parent's name if the person is under 18
  val parentName = new Screen {
    val parentName = field(S ? "Mom or Dad's name", "",
                           valMinLen(2, S ? "Name Too Short"),
      valMaxLen(40, S ? "Name Too Long"))
  }

  // we ask for the favorite pet
  val favoritePet = new Screen {
    val petName = field(S ? "Pet's name", "",
                        valMinLen(2, S ? "Name Too Short"),
                        valMaxLen(40, S ? "Name Too Long"))
  }

  // what to do on completion of the wizard
  def finish() {
    S.notice("Thank you for registering your pet")
    completeInfo.set(true)
  }
}

object WizardChallenge extends Wizard {
  val page1 = new Screen {
    val info = field(S ? "Page one entry", "")
  }

  val page2 = new Screen {
    override def screenTop = <span>Page one field is {page1.info}</span>

    val info = field(S ? "Page two entry", "")
  }

  val page3 = new Screen {
    override def screenTop = <span>Page one field is {page1.info}<br/>Page two field is {page2.info}</span>
  }

  def finish() {
    S.notice("Finished the challenge")
  }
}

object PersonScreen extends LiftScreen {
  object person extends ScreenVar(Person.create)

  override def screenTop =
  <b>A single screen with some input validation</b>

  _register(() => person.is)

  val shouldSave = field("Save ?", false)

  val likeCats = builder("Do you like cats?", "") ^/
  (s => if (Helpers.toBoolean(s)) Nil else "You have to like cats") make

  def finish() {
    S.notice("Thank you for adding "+person.is)
    if (shouldSave.is) {
      person.is.save
      S.notice(person.is.toString+" Saved in the database")
    }
  }
}

object AskAboutIceCream1 extends LiftScreen {
  val flavor = field(S ? "What's your favorite Ice cream flavor", "")

  def finish() {
    S.notice("I like "+flavor.is+" too!")
  }
}

object AskAboutIceCream2 extends LiftScreen {
  val flavor = field(S ? "What's your favorite Ice cream flavor", "",
                     trim,
                     valMinLen(2, "Name too short"),
                     valMaxLen(40, "That's a long name"))

  def finish() {
    S.notice("I like "+flavor.is+" too!")
  }
}

object AskAboutIceCream3 extends LiftScreen {
  val flavor = field(S ? "What's your favorite Ice cream flavor", "",
                     trim, valMinLen(2,S ? "Name too short"),
                     valMaxLen(40,S ? "That's a long name"))

  val sauce = field(S ? "Like chocalate sauce?", false)

  def finish() {
    if (sauce) {
      S.notice(flavor.is+" tastes especially good with chocolate sauce!")
    }
    else S.notice("I like "+flavor.is+" too!")
  }
}

object AskAboutIceCream4 extends LiftScreen {
  val flavor = field(S ? "What's your favorite Ice cream flavor", "",
                     trim, valMinLen(2,S ? "Name too short"),
                     valMaxLen(40,S ? "That's a long name"))

  val sauce = field(S ? "Like chocalate sauce?", false)

  override def validations = notTooMuchChocolate _ :: super.validations

  def notTooMuchChocolate(): Errors = {
    if (sauce && flavor.toLowerCase.contains("chocolate")) "That's a lot of chocolate"
    else Nil
  }

  def finish() {
    if (sauce) {
      S.notice(flavor.is+" tastes especially good with chocolate sauce!")
    }
    else S.notice("I like "+flavor.is+" too!")
  }
}

}
}
}
