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
package snippet {

import _root_.net.liftweb.common._
import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util.Helpers._
import _root_.net.liftweb.sitemap._
import _root_.scala.xml._
import _root_.com.hellolift.model.Entry
import _root_.com.hellolift.model.User

class BlogUtil {
  def entry = (new Entry).author(User.currentUser).toForm(Full("Post"),
				 (t: Entry) => {
				   t.save
				   S.redirectTo("/view?id=" + t.id)})

  def viewentry(xhtml : Group) : NodeSeq = {
    val t = Entry.find(S.param("id"))
    t.map(t =>
      bind("entry", xhtml,
	   "name" -> t.title.toString,
	   "body" -> t.body.toString)) openOr <span>Not found!</span>
  }

  def _entryview(e : Entry) : Node = {
    <div>
    <strong>{e.title}</strong><br />
    <span>{e.body}</span>
    </div>
  }

  def viewblog(xhtml : Group) : NodeSeq = {
    // Find all Entries by author using the parameter
    val t = Entry.findAll(By(Entry.author, toLong(S.param("id"))),
			OrderBy(Entry.id, Descending), MaxRows(20))
    t match {
      // If no 'id' was requested, then show a listing of all users.
      case Nil => User.findAll().map(u => <span><a href={"/blog?id=" + u.id}>
				      {u.firstName + " " + u.lastName}</a>
				      <br /></span>)
      case entries =>
	<lift:comet type="DynamicBlogView" name={toLong(S.param("id")).toString}>
          <blog:view>Loading...</blog:view>
        </lift:comet>
    }
  }

  def requestDetails: NodeSeq = {
    <span>
    <p>
    Request's Locale: {S.locale}
    </p>
    <p>
    Request(User): Locale : {User.currentUser.map(ignore => S.locale.toString).openOr("No User logged in.")}
    </p>
    </span>
  }
}
}
}
