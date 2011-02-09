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

import org.specs._
import org.specs.Sugar._
import org.specs.runner._
import net.liftweb.example.model._
import net.liftweb.http.{ S, Req, LiftSession }
import net.liftweb.common.{ Full, Empty }
import org.mockito.Matchers._

/*
class WikiSpecTest extends Specification with MockEntries with JUnit {
  "In the following spec, 'pageName' refers to the value of the S parameter 'wiki_page'" +
  "The 'main' function" should { createMocks.before
    "return all existing entries if pageName is 'all'" in {
      withEntries(WikiEntry.create.name("EntryOne"), WikiEntry.create.name("EntryTwo"))
      userRequests("all")
      inSession {
        wikiMain must \\("a", Map("href" -> "/wiki/EntryOne"))
        wikiMain must \\("a", Map("href" -> "/wiki/EntryTwo"))
      }
    }
    "return a new page with a form for a 'HomePage' entry if the wiki_page parameter is not specified" in {
      withNoEntries; userRequests("nothing")
      inSession {
        wikiMain must \\("form", Map("action" -> "/wiki/HomePage", "method" -> "GET"))
      }
    }
    "return a new page with a form for a 'NewEntry' entry if there is no entry with the name 'NewEntry' in the database" in {
      withNoEntries; userRequests("NewEntry")
      inSession {
        wikiMain must \\("form", Map("action" -> "/wiki/NewEntry", "method" -> "GET"))
      }
    }
    "return an existing entry if there is an entry named 'ExistingEntry' in the database" in {
      withEntries(WikiEntry.create.name("ExistingEntry")); userRequests("ExistingEntry")
      inSession {
        wikiMain must \\("form", Map("action" -> "/wiki/ExistingEntry", "method" -> "GET"))
      }
    }
  }
  "A newly created entry" should { createMocks.before
    "be named 'HomePage' if pageName is not specified" in {
      withNoEntries; userRequests("nothing")
      inSession {
        wikiMain.toString must include("Create Entry named HomePage")
      }
    }
    "be named 'pageName' if pageName is specified" in {
      withNoEntries; userRequests("MyPage")
      inSession {
        wikiMain.toString must include("Create Entry named MyPage")
      }
    }
  }
}*/

/*
import net.liftweb.mapper._
import net.liftweb.example.model._
import net.liftweb.example.snippet._

trait MockEntries extends MockRequest {
  var wikiEntries: MetaWikiEntry = _
  var requested = "all"
  def wikiMain = {
    trait MockedMetaWikiEntry extends MetaWikiEntry {
      override def find(q: QueryParam[WikiEntry]*) = wikiEntries.find(q:_*)
      override def findAll(q: QueryParam[WikiEntry]*) = wikiEntries.findAll(q:_*)
      override def create = wikiEntries.create
      override def findAll = wikiEntries.findAll
    }
    val wiki = new Wiki with MockedMetaWikiEntry
    wiki.main
  }
  override def createMocks = {
    super.createMocks
    wikiEntries = mock[MetaWikiEntry]
  }
  def userRequests(page: String) {
    if (page == "nothing")
      unsetParameter("wiki_page")
    else
      setParameter("wiki_page", page)
    requested = page
  }
  def withNoEntries = {
    wikiEntries.find(anyObject[QueryParam[WikiEntry]]) returns Empty
    wikiEntries.create returns new WikiEntry
  }
  def withEntries(entries: WikiEntry*) = {
    if (entries.isEmpty)
      wikiEntries.find(anyObject[QueryParam[WikiEntry]]) returns Empty
    else if (requested == "all")
      wikiEntries.findAll returns entries.toList
    else
      wikiEntries.find(anyObject[QueryParam[WikiEntry]]) returns Full(entries(0))
    wikiEntries.findAll(anyObject[QueryParam[WikiEntry]]) returns entries.toList
  }
}
import javax.servlet.http._
import org.specs.mock.Mockito

trait MockRequest extends Mockito {
  var request = mock[Req]
  var httpRequest = mock[HttpServletRequest]
  var session = mock[LiftSession]
  def createMocks: Unit = {
    request = mock[Req]
    httpRequest = mock[HttpServletRequest]
    session = mock[LiftSession]
    request.request returns httpRequest
  }
  def inSession(f: => Any) {
    S.init(request, session) {
      f
    }
  }
  def unsetParameter(name: String) {
    request.param(name) returns None
  }
  def setParameter(name: String, value: String) {
    request.param(name) returns Some(value)
  }
}*/
}
}
}
