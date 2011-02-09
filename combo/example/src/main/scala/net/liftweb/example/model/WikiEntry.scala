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

package net.liftweb {
package example {
package model {

import _root_.net.liftweb.mapper._

/**
 * The singleton that has methods for accessing the database
 */
object WikiEntry extends WikiEntry with LongKeyedMetaMapper[WikiEntry]

/**
 * An O-R mapped wiki entry
 */
class WikiEntry extends LongKeyedMapper[WikiEntry] with IdPK {
  def getSingleton = WikiEntry // what's the "meta" object

  // the name of the entry
  object name extends MappedString(this, 32) {
    override def dbIndexed_? = true // indexed in the DB
  }

  // the text of the entry
  object entry extends MappedTextarea(this, 8192) {
    override def textareaRows  = 10
    override def textareaCols = 50
  }
}
}
}
}
