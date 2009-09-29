package com.hellolift.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.base._
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

