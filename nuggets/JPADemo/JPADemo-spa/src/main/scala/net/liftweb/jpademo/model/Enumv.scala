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

/* adds a valueOf function, assumes name is defined
add optional description */
trait Enumv  {

  this: Enumeration =>

  private var nameDescriptionMap = scala.collection.mutable.Map[String, String]()

  /* store a name and description for forms */
  def Value(name: String, desc: String) : Value = {
    nameDescriptionMap += (name -> desc)
    new Val(name)
  }

    /* get description if it exists else name */
  def getDescriptionOrName(ev: this.Value) = {
    try {
      nameDescriptionMap(""+ev)
    } catch {
      case e: NoSuchElementException => ev.toString
    }
  }

  /* get name description pair list for forms */
  def getNameDescriptionList =  this.map(v => (v.toString, getDescriptionOrName(v))).toList
}
}
}
}

