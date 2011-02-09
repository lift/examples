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
package snippet {

import _root_.scala.xml._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.S._
import _root_.net.liftweb.http.SHtml._
import _root_.com.skittr.model._
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util.Helpers._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.com.skittr.actor._

class UserMgt {
  def login_panel(xhtml: Group): NodeSeq = {
    <h1>acct</h1> ++
    (if (logged_in_?) {
      <ul><li><a href="/logout">log out</a></li></ul>
    } else {
        var username = ""
        var pwd = ""
        def testPwd {
           User.find(By(User.name, username)).filter(_.password.match_?(pwd)).map{
             u => S.set("user_name", u.name)
             S.redirectTo("/")
           }.openOr(S.error("Invalid Username/Password"))
        }
      <form method="post" action={S.uri}>
      <table>
      <tr><td>name:</td><td>{text("", u => username = u)}</td></tr>
      <tr><td>pwd:</td><td>{password("", p => pwd = p)}</td></tr>
      <tr><td><a href="/new_acct">new acct</a></td><td>{submit("login", testPwd _)}</td></tr>
      </table>
      </form>
    })
  }

  def new_account: NodeSeq = {
    if (logged_in_?) {S.error("Can't create new account if you're logged in"); S.redirectTo("/")}
    else {
      val invokedAs = S.invokedAs
      val theUser = new User
      def newAccount(ignore: NodeSeq): NodeSeq = {
        def saveMe(in: User) {
          // validate the user
          val issues = theUser.validate

          // if there are no issues, set the friendly name, destroy the token, log the user in and send them to the home page
          if (issues.isEmpty) {
            theUser.save
            S.set("user_name", theUser.name)
            S.notice("welcome to skittr")
            redirectTo("/")
          }

          // This method tells lift that if we get another call to the same snippet during a page
          // reload, don't create a new instance, just invoke the innerAccept function which has
          // "user" bound to it
          S.mapSnippet(invokedAs, newAccount)

          // whoops... we have issues, display them to the user and continue loading this page
          error(issues)
        }
        <form method="post" action={S.uri}>
        <table>{
          theUser.toForm(Empty, saveMe _)
        }
        <tr><td>&nbsp;</td><td><input type="submit" value="Create New Account"/></td></tr></table>
        </form>
      }

      newAccount(Text(""))
    }
  }

  def logout: NodeSeq = {
    S.unset("user_name")
    S.redirectTo("/")
  }

  private def friendList(user: UserIdInfo): NodeSeq = <ul>{
    user.friends.map(f => <li><a href={"/user/"+f}>{f}</a></li>)}</ul> ++ (
      (for (curUser <- S.get("user_name");
            ua <- UserList.find(curUser);
            userInfo <- (ua !? (400L, GetUserIdAndName)) match {case Full(u: UserIdInfo) => Full(u) ; case _ => Empty}) yield {
        if (userInfo.friends.contains(user.name)) <a href={"/unfriend/"+user.name}>Unfriend</a>
        else <a href={"/friend/"+user.name}>Befriend</a>
      }
      ) openOr Text(""))

  def show_user(xhtml: Group): NodeSeq = {
    (for (userName <- S.param("user");
          userActor <- UserList.find(userName);
          user <- (userActor !? (400L, GetUserIdAndName)) match {case Full(u: UserIdInfo) => Full(u) ; case _ => Empty};
          messages <- (userActor !? (400L, GetMessages)) match {case Full(m : Messages) => Full(m.messages); case _ => Empty}) yield {
      bind("sk", xhtml, "username" -> (user.name+" -> "+user.fullName), "content" -> friendList(user)) ++
        messages.flatMap{
        msg =>
        Helpers.bind("sk", xhtml, "username" -> (msg.who+" @ "+toInternetDate(msg.when)), "content" -> msg.text)
      }
    }) openOr {S.error("User "+(S.param("user") openOr "")+" not found"); S.redirectTo("/")}
  }

  def watch_or_show(xhtml: Group): NodeSeq = {
   (for (userName <- S.get("user_name")) yield {
    <lift:comet type="watch_user" name={userName}>
    {
      xhtml.nodes
    }
    </lift:comet>
    }) openOr {
      Helpers.bind("sk", xhtml, "username" -> <a href="/new_acct">Create a New Account</a>,
          "content" -> <span>See what others are up to:<ul>{
            UserList.randomUsers(40).flatMap {
              u =>
              <li><a href={"/user/"+u}>{u}</a></li>
            }
          }</ul></span>)
    }
  }

  def friend: NodeSeq = {
    (for (userName <- S.get("user_name");
          userActor <- UserList.find(userName);
          toFriend <- S.param("user")) yield {
      S.notice("You've add "+toFriend+" your your list of friends")
       userActor ! AddFriend(toFriend)
    }) openOr {
      S.error("Unable to friend")
    }

    S.redirectTo("/")
  }

  def unfriend: NodeSeq = {
    (for (userName <- S.get("user_name");
          userActor <- UserList.find(userName);
          toUnfriend <- S.param("user")) yield {
      S.notice("You've removed "+toUnfriend+" from your list of friends")
       userActor ! RemoveFriend(toUnfriend)
    }) openOr {
      S.error("Unable to unfriend")
    }

    S.redirectTo("/")
  }

  def random(xhtml: Group): NodeSeq = {
    Helpers.bind("sk", xhtml, "username" -> "A Random List of Users",
        "content" -> <span>See what others are up to:<ul>{
          UserList.randomUsers(40).flatMap {
            u =>
            <li><a href={"/user/"+u}>{u}</a></li>
          }
        }</ul></span>)
  }

  def cur_name:  MetaData = new UnprefixedAttribute("name", Text(S.param("user").openOr("")), Null)

  def logged_in_? = S.get("user_name").isDefined
}
}
}
