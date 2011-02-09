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
package snippet {

/*

import _root_.net.liftweb.example.model._
import _root_.scala.xml.{NodeSeq, Text, Group}
import _root_.net.liftweb.http.{S, SHtml}
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.http.S._
import _root_.net.liftweb.util._
import _root_.net.liftweb.util.Helpers._
import _root_.net.liftweb.textile._

// show determines which one is used. bind hooks the content into the lift view
case class BindChoice(show: Boolean, bind: () => NodeSeq)

class Wiki extends MetaWikiEntry {

  def uriFor(path:String) = "/wiki/" + path

  /**
   * Display the Textile marked up wiki or an edit box
   */
  def main: NodeSeq = {
    val pageName = S.param("wiki_page") openOr "HomePage" // set the name of the page
    def showAll = {
      findAll(OrderBy(WikiEntry.name, Ascending)).flatMap(entry =>
      <div><a href={uriFor(entry.name)}>{entry.name}</a></div>)
    }

    if (pageName == "all") showAll // if the page is "all" display all the pages
    else {
      // find the entry in the database or create a new one
      val entry = find(By(WikiEntry.name, pageName)) openOr create.name(pageName)

      // is it a new entry?
      val isNew = !entry.saved_?

      // show edit or just display
      val edit = isNew || (S.param("param1").map(_ == "edit") openOr false)

      <span><a href={uriFor("all")}>Show All Pages</a><br/>{
        if (edit) editEntry(entry, isNew, pageName)
        else TextileParser.toHtml(entry.entry,
				  Some(TextileParser.DefaultRewriter("/wiki"))) ++
        <br/><a href={uriFor(pageName+"/edit")}>Edit</a> // and add an "edit" link
      }</span>
    }
  }

  def choosebind(xhtml : NodeSeq) = {
    def pageName = S.param("wiki_page") openOr "HomePage" // set the name of the page

    def showAll = BindChoice((pageName == "all"), () => bind("pages",
      (xhtml \\ "showAll").filter(_.prefix == "wiki").toList.head.child,
      TheBindParam("all", findAll(OrderBy(WikiEntry.name, Ascending)).flatMap(entry =>
      <div><a href={"/wikibind/"+entry.name}>{entry.name}</a></div>))))

    // find the entry in the database or create a new one
    def entry = find(By(WikiEntry.name, pageName)) openOr create.name(pageName)

    // is it a new entry?
    def isNew = !entry.saved_?
    def toEdit = isNew || (S.param("param1").map(_ == "edit") openOr false)

    def edit = BindChoice(toEdit, () => bind("edit",
      (xhtml \\ "editting").filter(_.prefix == "wiki").toList.head.child,
      "form" -> editEntry(entry, isNew, pageName)))

    def view = BindChoice(!toEdit, () => bind("view",
      (xhtml \\ "displaying").filter(_.prefix == "wiki").toList.head.child,
      TheBindParam("name", Text(pageName)),
      TheBindParam("value", (TextileParser.toHtml(entry.entry,
						  Some(TextileParser.DefaultRewriter("/wiki"))) ++
      <br/><a href={uriFor(pageName+"/edit")}>Edit</a>))))

    (showAll :: edit :: view :: Nil).find(_.show == true).map(_.bind()) match {
      case Some(x) => x
      case _ => <span />
    }
  }

  private def editEntry(entry: WikiEntry, isNew: Boolean, pageName: String) = {
    val action = uriFor(pageName)
    val message = if (isNew) Text("Create Entry named "+pageName) else Text("Edit entry named "+pageName)
    val hobixLink = <span>&nbsp;<a href="http://hobix.com/textile/quick.html" target="_blank">Textile Markup Reference</a><br /></span>
    val cancelLink = <a href={uriFor(pageName)}>Cancel</a>
    val textarea = entry.entry.toForm
    val submitButton = SHtml.submit(isNew ? "Add" | "Edit", () => entry.save)
    <form method="GET" action={action}>{ // the form tag
          message ++
          hobixLink ++
          textarea ++ // display the form
          <br /> ++
          cancelLink ++
          Text(" ") ++
          submitButton
    }</form>
  }
}
*/
}
}
}
