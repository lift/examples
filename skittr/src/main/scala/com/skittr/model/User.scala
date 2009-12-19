package com.skittr.model

/*                                                *\
 (c) 2007 WorldWide Conferencing, LLC
 Distributed under an Apache License
 http://www.apache.org/licenses/LICENSE-2.0
 \*                                                 */

import _root_.net.liftweb.mapper._
import _root_.com.skittr.actor._
import _root_.net.liftweb.util.Helpers._
import _root_.java.util.regex._

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with KeyedMetaMapper[Long, User] {
  override def dbTableName = "users" // define the DB table name

  // define the order fields will appear in forms and output
  override def fieldOrder = id :: name :: firstName :: lastName :: email ::  password :: Nil

  // after we create a user in the database, add the user to the active actors
  override def afterCreate = UserList.startUser _ :: super.afterCreate


  /**
    * Calculate a random persiod of at least 2 minutes and at most 8 minutes
    */
  def randomPeriod: Long = 2.minutes + randomLong(6.minutes)
  // def randomPeriod: Long = 15.seconds + randomLong(45.seconds)

  def shouldAutogen_? = false

  // the number of test users to create
  def createdCount = 1000

  def createTestUsers {
    (1 to createdCount).foreach {
      i =>

      User.create.firstName("Mr.").lastName("User "+i).email("user"+i+"@skittr.com").
        password("my_pwd_"+i).name("test"+i).dontStart.saveMe
    }

    (1 to createdCount * 7).foreach {
      i =>
      val owner = randomLong(createdCount) + 1
      val friend = randomLong(createdCount) + 1
      if (owner != friend && Friend.count(By(Friend.friend, friend), By(Friend.owner, owner)) == 0) {
        Friend.create.owner(owner).friend(friend).save
      }
    }

  }

  val validName = Pattern.compile("^[a-z0-9_]{3,30}$")
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends ProtoUser[User] {
  def getSingleton = User // what's the "meta" server

  def wholeName = firstName+" "+lastName
  private var startMeUp = true

  // The Name of the User
  object name extends MappedString(this, 32) {
    // input filter for the user name
    override def setFilter = notNull _ :: toLower _ :: trim _ :: super.setFilter

    // validation for the user name
    override def validations = valMinLen(3, "Name too short") _ ::
     valRegex(User.validName, "The 'name' must be letters, numbers, or the '_' (underscore)") _ ::
     valUnique("The name '"+is+"' is already taken") _ ::
     super.validations

    override def dbIndexed_? = true
  }

  def dontStart = {
    startMeUp = false
    this
  }

  def shouldStart_? = startMeUp
}
