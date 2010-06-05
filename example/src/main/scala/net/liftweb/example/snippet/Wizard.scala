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
    val name = new Field with StringField {
      def name = S ? "First Name"

      override def validations = minLen(2, S ? "Name Too Short") ::
              maxLen(40, S ? "Name Too Long") :: super.validations
    }

    // and an age field
    val age = new Field with IntField {
      def name = S ? "Age"

      override def validations = minVal(5, S ?? "Too young") ::
              maxVal(120, S ? "You should be dead") :: super.validations
    }

    // choose the next screen based on the age
    override def nextScreen = if (age.is < 18) parentName else favoritePet
  }

  // We ask the parent's name if the person is under 18
  val parentName = new Screen {
    val parentName = new Field with StringField {
      def name = S ? "Mom or Dad's name"

      override def validations = minLen(2, S ? "Name Too Short") ::
              maxLen(40, S ? "Name Too Long") :: super.validations
    }
  }

  // we ask for the favorite pet
  val favoritePet = new Screen {
    val petName = new Field with StringField {
      def name = S ? "Pet's name"

      override def validations = minLen(2, S ? "Name Too Short") ::
              maxLen(40, S ? "Name Too Long") :: super.validations
    }
  }

  // what to do on completion of the wizard
  def finish() {
    S.notice("Thank you for registering your pet")
    completeInfo.set(true)
  }
}

object WizardChallenge extends Wizard {
  val page1 = new Screen {
    val info = new Field with StringField {
      def name = S ? "Page one entry"
    }
  }

  val page2 = new Screen {
    override def screenTop = <span>Page one field is{page1.info}</span>

    val info = new Field with StringField {
      def name = S ? "Page two entry"
    }
  }

  val page3 = new Screen {
    override def screenTop = <span>Page one field is{page1.info}<br/>Page two field is{page2.info}</span>
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

  val shouldSave = new Field with BooleanField {
    def name = "Save ?"
  }

  def finish() {
    if (shouldSave.is) {
      person.is.save
    }
  }
}}
}
}
