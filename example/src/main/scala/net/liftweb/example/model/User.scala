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
package model {

import _root_.net.liftweb._
import mapper._

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with KeyedMetaMapper[Long, User] {
  override def dbTableName = "users" // define the DB table name

  // define the order fields will appear in forms and output
  override lazy val fieldOrder = List(id, firstName, lastName,
				      email, password, textArea)
}

/**
 * An O-R mapped "User" class that includes first name, last name,
 * password and we add a "Personal Essay" to it
 */
class User extends ProtoUser[User] {
  def getSingleton = User

  // define an additional field for a personal essay
  object textArea extends MappedTextarea(this, 2048) {
    override def textareaRows  = 10
    override def textareaCols = 50
    override def displayName = "Personal Essay"
  }
}
}
}
}
