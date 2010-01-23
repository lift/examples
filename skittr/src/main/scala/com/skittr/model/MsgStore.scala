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

package com.skittr {
package model {

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
}
}
