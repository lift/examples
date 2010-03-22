/*
 * Copyright 2010 WorldWide Conferencing, LLC
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
import http._
import js._
import JsCmds._
import common._
import json._
import JsonAST._
import textile._

import scala.xml._

/**
 * Respond to JSON requests in a stateless dispatch
 */
object StatelessJson {
  def init() {
    // register the JSON handler
    LiftRules.statelessDispatchTable.append {
      case r @ Req("stateless_json_call" :: Nil, _, PostRequest) => () => handleJson(r)
    }
  }

  implicit def iterableToBox[X](in: Iterable[X]): Box[X] = in.toList.headOption

  def handleJson(req: Req): Box[LiftResponse] =
  for {
    json <- req.json // get the JSON
    JObject(List(JField("command", JString(cmd)), JField("params", JString(params)))) <- json // extract the command
  } yield JavaScriptResponse(SetHtml("json_result",cmd match { // build the response
        case "show" => Text(params)
        case "textile" =>  TextileParser.toHtml(params, Empty)
        case "count" => Text(params.length+" Characters")
        case x => <b>Problem... didn't handle JSON message {x}</b>
      }))
}
    }
  }
}
