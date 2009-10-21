/*
 * Copyright 2007-2009 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package net.liftweb.example.comet

import _root_.net.liftweb._
import http._
import common._
import util._
import _root_.net.liftweb.example.model._

object WebServices {
  // register the WebServices with the dispatcher
  def init() {
    LiftRules.dispatch.append(NamedPF("Web Services Example") {
        case Req("webservices" :: "all_users" :: Nil, _, GetRequest) =>
          () => Full(all_users())

        case Req("webservices" :: "add_user" :: Nil, _, rt)
          if rt == GetRequest || rt == PostRequest =>
          () => Full(add_user())
      })
  }

  // List the XML for all users
  def all_users(): XmlResponse =
  XmlResponse(
    <all_users>
      {
        User.findAll.map(_.toXml)
      }
    </all_users>)


  // extract the parameters, create a user
  // return the appropriate response
  def add_user(): LiftResponse =
  (for {
      firstname <- S.param("firstname") ?~ "firstname parameter missing"
      lastname <- S.param("lastname") ?~ "lastname parameter missing"
      email <- S.param("email") ?~ "email parameter missing"
    } yield {
      val u =User.create.firstName(firstname).lastName(lastname).email(email).
      textArea(S.param("textarea") openOr "")

      S.param("password").map{v => u.password(v)}
      u.save
    }) match {
    case Full(success) => XmlResponse(<add_user success={success.toString}/>)
    case Failure(msg, _, _) => NotAcceptableResponse(msg)
    case _ => NotFoundResponse()
  }
}
