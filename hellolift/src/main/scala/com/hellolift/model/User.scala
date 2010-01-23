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

package com.hellolift {
package model {

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.common._
import _root_.net.liftweb.util._

/**
 * The singleton that has methods for accessing the database. LiftNote: 2
 */
object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users"
  override def screenWrap = Full(<lift:surround with="default" at="content">
			       <lift:bind /></lift:surround>) // LiftNote: 6
  override def signupFields = firstName :: lastName :: email :: locale :: timezone :: password :: blogtitle :: Nil
  override val skipEmailValidation = true // LiftNote: 4

  override val basePath: List[String] = "user_mgt" :: "usr" :: Nil
}


/**
 * An O-R mapped "User" class that includes first name, last name, password. LiftNote: 1
 */
class User extends MegaProtoUser[User] {
  def getSingleton = User // what's the "meta" server

  object blogtitle extends MappedString(this, 128)
}
}
}

