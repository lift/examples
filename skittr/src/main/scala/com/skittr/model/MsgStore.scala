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
object MsgStore extends MsgStore with KeyedMetaMapper[Long, MsgStore] {
  override def dbTableName = "messages" // define the DB table name
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class MsgStore extends KeyedMapper[Long, MsgStore] {
  def getSingleton = MsgStore // what's the "meta" server
  def primaryKeyField = id

  object id extends MappedLongIndex(this)

  object message extends MappedString(this, 200)
  object who extends MappedLongForeignKey(this, User)
  object when extends MappedLong(this) {
    override def dbColumnName = "when_c"
    override def defaultValue = System.currentTimeMillis
  }
  object source extends MappedString(this, 16)
}
