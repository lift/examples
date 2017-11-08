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

class WizardChallenge extends Wizard {
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

class PersonScreen extends LiftScreen {
  object person extends ScreenVar(Person.create)

  override def screenTop =
  <b>A single screen with some input validation</b>

  addFields(() => person.is)

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

object VariableScreenInfo {
  private trait FEP {
    self: FieldIdentifier =>
    implicit def strToListFieldError(msg: String): List[FieldError] =
      List(FieldError(self, Text(msg)))
  }

  def name: BaseField = new BaseField with FEP {
    private var _name = ""
    def toForm: Box[NodeSeq] = Full(SHtml.text(_name, _name = _))

    def setFilter = Nil

    def validations = Nil

    def set(v: String) = {_name = v; v}
    def get = _name
    def is = get
    type ValueType = String

    def name = "Name"    

    override val uniqueFieldId: Box[String] = Full(Helpers.nextFuncName)

    def validate = if (_name.length >= 4) Nil
    else "Name must be 4 characters"
  }

  def address: BaseField = new BaseField with FEP {
    private var address = ""
    def toForm: Box[NodeSeq] = Full(SHtml.text(address, address = _))

    def setFilter = Nil

    def validations = Nil

    def set(v: String) = {address = v; v}
    def get = address
    def is = get
    type ValueType = String

    def name = "Address"

    override val uniqueFieldId: Box[String] = Full(Helpers.nextFuncName)

    def validate = if (address.length >= 3) Nil
    else "Address must be 3 characters"
  }

  def age: BaseField = new BaseField with FEP {
    private var age = 0
    def toForm: Box[NodeSeq] = Full(SHtml.text(age.toString,
                                               s => Helpers.asInt(s).map(age = _)))

    def set(v: Int) = {age = v; v}
    def get = age
    def is = get
    type ValueType = Int

    def setFilter = Nil

    def validations = Nil
    def name = "Age"

    override val uniqueFieldId: Box[String] = Full(Helpers.nextFuncName)

    def validate = if (age > 10) Nil
    else "Age must be greater than 10"

  }

  def selection: BaseField = new BaseField {
    private val opts = List("A", "B", "C", "Last")
    private var sel = Full("C")
    def toForm: Box[NodeSeq] = Full(SHtml.select(opts.map(a => a -> a), sel,
                                                 x => sel = Full(x)))
    def set(v: String) = {sel = Full(v); v}
    def name = "Selection Thing"
    def get = sel.open_!
    def is = get
    type ValueType = String

    def setFilter = Nil

    def validations = Nil

    override val uniqueFieldId: Box[String] = Full(Helpers.nextFuncName)

    def validate = Nil
  }

  def chooseFields: FieldContainer = 
    List(name, address, age, selection) filter {
      ignore => Helpers.randomInt(100) > 50
    } match {
      case Nil => chooseFields
      case xs => new FieldContainer {
        def allFields = xs
      }
    }
}

class VariableScreen extends LiftScreen {
  object fields extends ScreenVar(VariableScreenInfo.chooseFields)

  override def screenTop =
  <b>A single screen with variable fields</b>

  addFields(() => fields.is)

  def finish() {
    S.notice("You've completed the screen")
  }
}

class AskAboutIceCream1 extends LiftScreen {
  val flavor = field(S ? "What's your favorite Ice cream flavor", "")

  def finish() {
    S.notice("I like "+flavor.is+" too!")
  }
}

class AskAboutIceCream2 extends LiftScreen {
  val flavor = field(S ? "What's your favorite Ice cream flavor", "",
                     trim,
                     valMinLen(2, "Name too short"),
                     valMaxLen(40, "That's a long name"))

  def finish() {
    S.notice("I like "+flavor.is+" too!")
  }
}

class AskAboutIceCream3 extends LiftScreen {
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

class AskAboutIceCream4 extends LiftScreen {
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
