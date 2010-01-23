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
 * Helper class to translate money amount for hibernate
 */
abstract class CurrencyUserType[CZ <: CurrencyZone](cz: CZ) extends UserType {

  type MyCurrency = CZ#Currency

  val SQL_TYPES = Array(Types.NUMERIC.asInstanceOf[Int])

  override def sqlTypes() = SQL_TYPES

  override def returnedClass = cz.CurrencyUnit.getClass

  override def equals(x: Object, y: Object): Boolean = {
    if (x == null || y == null) return false
    else return x == y
  }

  override def hashCode(x: Object) = x.hashCode

  override def nullSafeGet(resultSet: ResultSet, names: Array[String], owner: Object): Object = {
    val dollarVal = resultSet.getBigDecimal(names(0))
    if (resultSet.wasNull()) return cz.make(0)
    else return cz.make(new BigDecimal(dollarVal))
  }

  override def nullSafeSet(statement: PreparedStatement, value: Object, index: Int): Unit = {
    if (value == null) {
      statement.setNull(index, Types.NUMERIC)
    } else {
      val dollarVal = value.asInstanceOf[MyCurrency]
      statement.setBigDecimal(index, dollarVal.amount.bigDecimal)
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

