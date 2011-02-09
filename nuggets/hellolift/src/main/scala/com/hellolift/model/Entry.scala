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

package com.hellolift {
package model {

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.common._
import _root_.net.liftweb.util._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._

import _root_.com.hellolift.controller.BlogCache
import _root_.com.hellolift.controller.BlogCache._
import _root_.com.hellolift.controller.AddEntry

object Entry extends Entry with KeyedMetaMapper[Long, Entry] {
  override def dbTableName = "entries"
  // sitemap entry
  val sitemap = List(Menu(Loc("CreateEntry", List("entry"),
			      "Create An Entry",
			      If(User.loggedIn_? _, "Please login"))),
		     Menu(Loc("ViewEntry", List("view"),
			      "View An Entry", Hidden)),
		     Menu(Loc("ViewBlog", List("blog"), "View Blog")))

  // Once the transaction is committed, fill in the blog cache with this entry.
  override def afterCommit =
    ((entry: Entry) => {BlogCache.cache ! AddEntry(entry, entry.author.is)}) :: Nil
}

class Entry extends KeyedMapper[Long, Entry] {
  def getSingleton = Entry // what's the "meta" server
  def primaryKeyField = id

  // Fields
  object id extends MappedLongIndex(this)
  object author extends MappedLongForeignKey(this, User) {
    override def dbDisplay_? = false
  }
  object title extends MappedString(this, 128)
  object body extends MappedTextarea(this, 20000) { // Lift Note: 7
    override def setFilter = notNull _  :: trim _ :: crop _ :: super.setFilter
  }
}
}
}
