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
package example {
package lib {

import _root_.net.liftweb._
import textile._
import common._
import util._
import Helpers._
import http._
import mapper._
import sitemap._
import Loc._

import example._
import model._

import scala.xml.{Text, NodeSeq}

/**
 * A wiki location
 *
 * @param page - the name of the page
 * @param edit - are we viewing or editing the page?
 */
case class WikiLoc(page: String, edit: Boolean) {

  /**
   * Get the underly database record for this page
   */
  lazy val record: WikiEntry =
  WikiEntry.find(By(WikiEntry.name, page)) openOr
  WikiEntry.create.name(page)
}

/**
 * The WikiStuff object that provides menu, URL rewriting,
 * and snippet support for the page that displays wiki contents
 */
object WikiStuff extends Loc[WikiLoc] {
  object AllLoc extends WikiLoc("all", false)

  // the name of the page
  def name = "wiki"

  // the default parameters (used for generating the menu listing)
  def defaultValue = Full(WikiLoc("HomePage", false))

  // no extra parameters
  def params = List(Unless(() => Props.inGAE || Props.productionMode, "Disabled for GAE"))

  // is the current page an "edit" or "view"
  def currentEdit = requestValue.is.map(_.edit) openOr false

  /**
   * Check for page-specific snippets and
   * do appropriate dispatching
   */
  override val snippets: SnippetTest = {
    case ("wiki", Full(AllLoc)) => showAll _
    case ("wiki", Full(wp @ WikiLoc(_ , true))) => editRecord(wp.record) _
    case ("wiki", Full(wp @ WikiLoc(_ , false)))
      if !wp.record.saved_? => editRecord(wp.record) _

    case ("wiki", Full(wp: WikiLoc)) => displayRecord(wp.record) _
  }


  /**
   * Generate a link based on the current page
   */
  val link =
  new Loc.Link[WikiLoc](List("wiki"), false) {
    override def createLink(in: WikiLoc) = {
      if (in.edit)
      Full(Text("/wiki/edit/"+urlEncode(in.page)))
      else
      Full(Text("/wiki/"+urlEncode(in.page)))
    }
  }

  /**
   * What's the text of the link?
   */
  val text = new Loc.LinkText(calcLinkText _)


  def calcLinkText(in: WikiLoc): NodeSeq =
  if (in.edit)
  Text("Wiki edit "+in.page)
  else
  Text("Wiki "+in.page)

  /**
   * Rewrite the request and emit the type-safe parameter
   */
  override val rewrite: LocRewrite =
  Full(NamedPF("Wiki Rewrite") {
      case RewriteRequest(ParsePath("wiki" :: "edit" :: page :: Nil, _, _,_),
                          _, _) =>
        (RewriteResponse("wiki" :: Nil), WikiLoc(page, true))

      case RewriteRequest(ParsePath("wiki" :: page :: Nil, _, _,_),
                          _, _) =>
        (RewriteResponse("wiki" :: Nil), WikiLoc(page, false))

    })

  def showAll(in: NodeSeq): NodeSeq =
  WikiEntry.findAll(OrderBy(WikiEntry.name, Ascending)).flatMap(entry =>
    <div><a href={url(entry.name)}>{entry.name}</a></div>)

  def url(page: String) = createLink(WikiLoc(page, false))


  def editRecord(r: WikiEntry)(in: NodeSeq): NodeSeq =
  <span>
    <a href={createLink(AllLoc)}>Show All Pages</a><br />
    {
      val isNew = !r.saved_?
      val pageName = r.name.is
      val action = url(pageName)
      val message =
      if (isNew)
      Text("Create Entry named "+pageName)
      else
      Text("Edit entry named "+pageName)

      val hobixLink = <span>&nbsp;<a href="http://hobix.com/textile/quick.html" target="_blank">Textile Markup Reference</a><br /></span>

      val cancelLink = <a href={action}>Cancel</a>
      val textarea = r.entry.toForm

      val submitButton = SHtml.submit(isNew ? "Add" | "Edit", () => r.save)

      <form method="post" action={action}>{ // the form tag
          message ++
          hobixLink ++
          textarea ++ // display the form
          <br /> ++
          cancelLink ++
          Text(" ") ++
          submitButton
        }</form>
    }

  </span>

  def displayRecord(entry: WikiEntry)(in: NodeSeq): NodeSeq =
  <span>
    <a href={createLink(AllLoc)}>Show All Pages</a><br />
    {TextileParser.toHtml(entry.entry, textileWriter)}

    <br/><a href={createLink(WikiLoc(entry.name, true))}>Edit</a>
  </span>

  import TextileParser._

  val textileWriter = Some((info: WikiURLInfo) =>
    info match {
      case WikiURLInfo(page, _) =>
        (stringUrl(page), Text(page), None)
    })

  def stringUrl(page: String): String =
  url(page).map(_.text) getOrElse ""


}
}
}
}
