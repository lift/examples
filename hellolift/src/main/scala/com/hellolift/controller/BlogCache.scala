package com.hellolift.controller

import _root_.net.liftweb.mapper._
import _root_.scala.collection.mutable.Map
import _root_.scala.actors.Actor
import _root_.scala.actors.Actor.{react}
import _root_.com.hellolift.model.Entry

/**
 * An asynchronous cache for Blog Entries built on top of Scala Actors.
 */
class BlogCache extends Actor {
  def act = loop(Map(), Map())

  def getEntries(id : Long) : List[Entry] = Entry.findAll(By(Entry.author, id), OrderBy(Entry.id, Descending), MaxRows(20))

  /**
   * This will seem strange to imperative programmers who are expecting the
   * cache to be in a value. The cache is maintained in the arguments to this
   * function that is tail-called.
   */
  def loop(cache : Map[Long, List[Entry]], sessions : Map[Long, List[Actor]]) {
    react {
      case AddBlogWatcher(me, id) =>
	// When somebody new starts watching, add them to the sessions and send
	// an immediate reply.
	val blog = cache.getOrElse(id, getEntries(id)).take(20)
	reply(BlogUpdate(blog))
	cache += (id -> blog)
        sessions += (id -> (me :: sessions.getOrElse(id, Nil)))
        loop(cache, sessions)
      case AddEntry(e, id) =>
	// When an Entry is added, place it into the cache and reply to the clients with it.
	cache += (id -> (e :: cache.getOrElse(id, getEntries(id))))
        // Now we have to notify all the listeners
        sessions.getOrElse(id, Nil).foreach(_ ! BlogUpdate(cache.getOrElse(id, Nil)))
	loop(cache, sessions)
      case DeleteEntry(e, id) =>
	// When an Entry is deleted
	cache += (id -> cache.getOrElse(id, getEntries(id)).remove(_ == e))
        sessions.getOrElse(id, Nil).foreach(_ ! BlogUpdate(cache.getOrElse(id, Nil)))
	loop(cache, sessions)
      case EditEntry(e, id) =>
	// It's easier to just re-query the database than to slice an dice the list. (for now)
	cache += (id -> getEntries(id))
	loop(cache, sessions)
      case _ => loop(cache, sessions)
    }
  }
}

case class AddEntry(e : Entry, id : Long) // id is the author id
case class EditEntry(e : Entry, id : Long) // id is the author id
case class DeleteEntry(e : Entry, id : Long) // id is the author id
case class AddBlogWatcher(me : Actor, id : Long) // id is the blog id

// A response sent to the cache listeners with the top 20 blog entries.
case class BlogUpdate(xs : List[Entry])

object BlogCache {
  val cache = {val ret = new BlogCache; ret.start; ret}
}

