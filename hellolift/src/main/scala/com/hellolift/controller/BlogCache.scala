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
package controller {

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.actor._
import _root_.net.liftweb.common._
import _root_.scala.collection.mutable.Map
import _root_.com.hellolift.model.Entry

/**
 * An asynchronous cache for Blog Entries built on top of Scala Actors.
 */
class BlogCache extends LiftActor {
  private var cache: Map[Long, List[Entry]] = Map()
  private var sessions : Map[Long, List[SimpleActor[Any]]] = Map() 

  def getEntries(id : Long) : List[Entry] = Entry.findAll(By(Entry.author, id), OrderBy(Entry.id, Descending), MaxRows(20))

  protected def messageHandler = 
    {
      case AddBlogWatcher(me, id) =>
	// When somebody new starts watching, add them to the sessions and send
	// an immediate reply.
	val blog = cache.getOrElse(id, getEntries(id)).take(20)
	reply(BlogUpdate(blog))
	cache += (id -> blog)
        sessions += (id -> (me :: sessions.getOrElse(id, Nil)))
      
      case AddEntry(e, id) =>
	// When an Entry is added, place it into the cache and reply to the clients with it.
	cache += (id -> (e :: cache.getOrElse(id, getEntries(id))))
        // Now we have to notify all the listeners
        sessions.getOrElse(id, Nil).foreach(_ ! BlogUpdate(cache.getOrElse(id, Nil)))
	
      case DeleteEntry(e, id) =>
	// When an Entry is deleted
	cache += (id -> cache.getOrElse(id, getEntries(id)).remove(_ == e))
        sessions.getOrElse(id, Nil).foreach(_ ! BlogUpdate(cache.getOrElse(id, Nil)))

      case EditEntry(e, id) =>
	// It's easier to just re-query the database than to slice an dice the list. (for now)
	cache += (id -> getEntries(id))

      case _ => 
    }
}

case class AddEntry(e : Entry, id : Long) // id is the author id
case class EditEntry(e : Entry, id : Long) // id is the author id
case class DeleteEntry(e : Entry, id : Long) // id is the author id
case class AddBlogWatcher(me : SimpleActor[Any], id : Long) // id is the blog id

// A response sent to the cache listeners with the top 20 blog entries.
case class BlogUpdate(xs : List[Entry])

object BlogCache {
  lazy val cache = new BlogCache // {val ret = new BlogCache; ret.start; ret}
}
}
}
