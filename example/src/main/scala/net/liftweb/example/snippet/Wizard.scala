/*
 * Copyright 2009 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package net.liftweb.example.snippet

import _root_.scala.xml.{NodeSeq, Text}
import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.wizard._
import _root_.net.liftweb.common._
import _root_.java.util.Date
import Helpers._

/**
 * An example of a wizard in Lift
 */
object MyWizard extends Wizard {
  object completeInfo extends WizardVar(false)

  // define the first screen
  val nameAndAge = new Screen {

    // it has a name field
    val name = new Field with StringField {
      def title = S ?? "First Name"

      override def validation = minLen(2, S ?? "Name Too Short") ::
          maxLen(40, S ?? "Name Too Long") :: super.validation
    }

    // and an age field
    val age = new Field with IntField {
      def title = S ?? "Age"

      override def validation = minVal(5, S ?? "Too young") ::
          maxVal(120, S ?? "You should be dead") :: super.validation
    }

    // choose the next screen based on the age
    override def nextScreen = if (age.is < 18) parentName else favoritePet
  }

  // We ask the parent's name if the person is under 18
  val parentName = new Screen {
    val parentName = new Field with StringField {
      def title = S ?? "Mom or Dad's name"

      override def validation = minLen(2, S ?? "Name Too Short") ::
          maxLen(40, S ?? "Name Too Long") :: super.validation
    }
  }

  // we ask for the favorite pet
  val favoritePet = new Screen {
    val petName = new Field with StringField {
      def title = S ?? "Pet's name"

      override def validation = minLen(2, S ?? "Name Too Short") ::
          maxLen(40, S ?? "Name Too Long") :: super.validation
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
      def title = S ?? "Page one entry"
    }
  }

  val page2 = new Screen {
    override def screenTop = <span>Page one field is {page1.info}</span>

    val info = new Field with StringField {
      def title = S ?? "Page two entry"
    }
  }

  val page3 = new Screen {
    override def screenTop = <span>Page one field is {page1.info}<br/>Page two field is {page2.info}</span>
  }

  def finish() {
    S.notice("Finished the challenge")
  }
}
