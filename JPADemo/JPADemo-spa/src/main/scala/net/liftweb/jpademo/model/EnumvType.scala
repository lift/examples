/*
 * Copyright 2008-2010 WorldWide Conferencing, LLC
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
package jpademo {
package model {

import _root_.java.io.Serializable
import _root_.java.sql.PreparedStatement
import _root_.java.sql.ResultSet
import _root_.java.sql.SQLException
import _root_.java.sql.Types

import _root_.org.hibernate.HibernateException
import _root_.org.hibernate.usertype.UserType

/**
 * Helper class to translate enum for hibernate
 */
abstract class EnumvType(val et: Enumeration with Enumv) extends UserType {

  val SQL_TYPES = Array({Types.VARCHAR})

  override def sqlTypes() = SQL_TYPES

  override def returnedClass = classOf[et.Value]

  override def equals(x: Object, y: Object): Boolean = {
    return x == y
  }

  override def hashCode(x: Object) = x.hashCode

  override def nullSafeGet(resultSet: ResultSet, names: Array[String], owner: Object): Object = {
    val value = resultSet.getString(names(0))
    if (resultSet.wasNull()) return null
    else {
      return et.valueOf(value).getOrElse(null)
    }
  }

  override def nullSafeSet(statement: PreparedStatement, value: Object, index: Int): Unit = {
    if (value == null) {
      statement.setNull(index, Types.VARCHAR)
    } else {
      val en = value.toString
      statement.setString(index, en)
    }
  }

  override def deepCopy(value: Object): Object = value

  override def isMutable() = false

  override def disassemble(value: Object) = value.asInstanceOf[Serializable]

  override def assemble(cached: Serializable, owner: Object): Serializable = cached

  override def replace(original: Object, target: Object, owner: Object) = original

}


}
}
}

