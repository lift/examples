package com.skittr.actor

/*                                                *\
 (c) 2007-2009 WorldWide Conferencing, LLC
 Distributed under an Apache License
 http://www.apache.org/licenses/LICENSE-2.0
\*                                                 */

import _root_.scala.actors._
import _root_.scala.actors.Actor._
import _root_.com.skittr.model._
import _root_.scala.collection.mutable.{HashMap}
import _root_.net.liftweb.mapper._
import _root_.java.util.concurrent.locks.ReentrantReadWriteLock
import _root_.net.liftweb.util._
import _root_.net.liftweb.base._
import _root_.net.liftweb.util.Helpers._
/**
  * A singleton the holds the map between user names and Actors that service
  * the users.  Right now, this assumes that the Actors are all local, but
  * it could also be extended to choose other machines (remote actors)
  * by using a hash on the username to choose the machine that's hosting
  * the user
  */
object UserList {
  private val set = new HashMap[String, UserActor]() // a map between the username and the Actor
  private val rwl = new ReentrantReadWriteLock // lots of readers, few writers
  private val r = rwl.readLock
  private val w = rwl.writeLock

  /**
    * Load all the users from the database and create actors for each of them
    */
  def create {
      def userToUserActor(u: User) = {
        val ua = new UserActor // create a new Actor
        ua.start // start it up
        ua !? Setup(u.id, u.name, u.wholeName) // tell it to set up
        Full(ua) // return it
      }
      // load all the users
      User.findMap()(userToUserActor).foreach (_ !? ConfigFollowers) // for each of the UserActors, tell them to configure their followers
  }

  // We've just added a new user to the system
  // add that user to the list
  def startUser(who: User) {
    if (who.shouldStart_?) {
    val ua = new UserActor
    ua.start
    ua ! Setup(who.id, who.name, who.wholeName)
    ua ! ConfigFollowers
    }
  }

  def shutdown = foreach(_ ! Bye) // shutdown by telling each of the Actors a "Bye" message

  private def writeLock[T](f: => T): T = {
    w.lock
    try {f} finally {w.unlock}
  }

  private def readLock[T](f: => T): T = {
    r.lock
    try {f} finally {r.unlock}
  }

  // iterate over all the actors in the system
  // and perform a function
  def foreach(f: UserActor => Any) = readLock(set.foreach(i => f(i._2)))

  // add a user to the list by mapping
  // the name to the UserActor
  def add(name: String, who: UserActor) = writeLock(set(name) = who)

  // find a user by name
  def find(name: String): Box[UserActor] = readLock(Box(set.get(name)))

  // remove a user
  def remove(name: String) = writeLock(set -= name)

  // Find a random set of about cnt users
  def randomUsers(cnt: Int) = {
    val percent = if (set.size == 0) 1.d else cnt.toDouble / set.size.toDouble
    readLock(set.filter(z => shouldShow(percent)).map(_._1))
  }
}
