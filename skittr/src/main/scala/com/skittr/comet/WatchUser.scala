package com.skittr.comet

/*                                                *\
 (c) 2007-2009 WorldWide Conferencing, LLC
 Distributed under an Apache License
 http://www.apache.org/licenses/LICENSE-2.0
 \*                                                 */

import _root_.scala.actors._
import _root_.scala.actors.Actor._
import _root_.net.liftweb.http._
import _root_.net.liftweb.util.Helpers._
import _root_.net.liftweb.util.{Helpers}
import _root_.net.liftweb.util.{Box, Full, Empty, Failure}
import _root_.scala.xml._
import _root_.com.skittr.actor._
import S._
import SHtml._
import _root_.com.skittr.model.{Friend, User}
import _root_.net.liftweb.mapper._

class WatchUser extends CometActor {
  private var userActor: Box[UserActor] = Empty
  private var messages: List[Message] = Nil
  override def defaultPrefix = Full("sk")

  private def getUser(ua: UserActor) = (ua !? (400L, GetUserIdAndName)) match {case Some(u: UserIdInfo) => Full(u) case _ => Empty}

  def render = {
    val ret: NodeSeq = (for (ua <- userActor;
          user <- getUser(ua)) yield {
	    bind("username" -> Text(user.name+" -> "+user.fullName) ,
		 "content" -> <span>{friendList(user) ++
		              ajaxForm(textarea("", msg => ua ! SendMessage(msg, "web")) % ("cols" -> "40") ++
                                       submit("msg", () => true))
                              }</span>) ++
	    messages.flatMap(msg => bind("username" -> Text(msg.who+" @ "+toInternetDate(msg.when)), "content" -> Text(msg.text)))
	  }) openOr bind("username" -> Text("N/A"), "content" -> Text("N/A"))
    ret
  }

  override def lowPriority : PartialFunction[Any, Unit] = {
      case Timeline(msg) =>
      messages = msg
      reRender(false)
  }


  override def localSetup {
    userActor = name.flatMap(name => UserList.find(name))
    userActor.foreach{ua => ua ! AddTimelineViewer ;  messages = (ua !? GetTimeline) match {case Timeline(m) => m; case _ => Nil}}
  }

  private def friendList(user: UserIdInfo): NodeSeq = <ul>{user.friends.map(f => <li><a href={"/user/"+f}>{f}</a>&nbsp;<a href={"/unfriend/"+f}>Unfriend</a></li>)}</ul>
}
