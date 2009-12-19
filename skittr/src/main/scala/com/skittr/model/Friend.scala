package com.skittr.model

/*                                                *\
 (c) 2007 WorldWide Conferencing, LLC
 Distributed under an Apache License
 http://www.apache.org/licenses/LICENSE-2.0
 \*                                                 */

import _root_.net.liftweb.mapper._
import DB._
import _root_.java.sql.Connection

/**
 * The singleton that has methods for accessing the database
 */
object Friend extends Friend with KeyedMetaMapper[Long, Friend] {
  override def dbTableName = "friends" // define the DB table name
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class Friend extends KeyedMapper[Long, Friend] {
  def getSingleton = Friend // what's the "meta" server
  def primaryKeyField = id

  object id extends MappedLongIndex(this)

  object owner extends MappedLongForeignKey(this, User)
  object friend extends MappedLongForeignKey(this, User)
  object when extends MappedLong(this) {
    override def dbColumnName = "when_c"
    override def defaultValue = System.currentTimeMillis
  }
}
