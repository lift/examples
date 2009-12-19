package com.hellolift.comet

import _root_.net.liftweb.http._
import _root_.net.liftweb.common._
import _root_.net.liftweb.util._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import _root_.scala.xml._

import _root_.com.hellolift.model.Entry
import _root_.com.hellolift.controller.BlogCache
import _root_.com.hellolift.controller.BlogUpdate
import _root_.com.hellolift.controller.AddBlogWatcher

class DynamicBlogView extends CometActor {
  override def defaultPrefix = Full("blog")
  var blogtitle = ""
  var blog : List[Entry] = Nil
  var blogid : Long = 0L

  def _entryview(e : Entry) : Node = {
    <div>
    <strong>{e.title}</strong><br />
    <span>{e.body}</span>
    </div>
  }

  // render draws the content on the screen.
  def render = {
    bind("view" -> <span>{blog.flatMap(e => _entryview(e))}</span>)
  }

  // localSetup is the first thing run, we use it to setup the blogid or
  // redirect them to / if no blogid was given.
  override def localSetup {
    name match {
      case Full(t) => this.blogid = Helpers.toLong(t)
    }

    // Let the BlogCache know that we are watching for updates for this blog.
    (BlogCache.cache !? AddBlogWatcher(this, this.blogid)) match {
      case BlogUpdate(entries) => this.blog = entries
    }
  }

  // lowPriority will receive messages sent from the BlogCache
  override def lowPriority : PartialFunction[Any, Unit] = {
    case BlogUpdate(entries : List[Entry]) => this.blog = entries; reRender(false)
  }
}
