package net.liftweb.example.snippet

import net.liftweb._
import http._
import SHtml._
import js._
import common._
import JsCmds._
import util._
import Helpers._

class Show {
  def render = {
    "#birthyear" #> SHtml.ajaxSelect(Show.yearsOptions,
                                     Show.yearsDefault,
                                     Show.yearsHandler) &
      "name=name" #> SHtml.ajaxText("", false, Show.nameHandler _) &
      "type=submit" #> submit("Submit", Show.submitHandler _)
  }
}

object Show {
  private var name: String = ""
  private var year: Int = 0
  val years: Map[String, Int] = Map("2006" -> 2006,
                                    "2007" -> 2007,
                                    "2008" -> 2008,
                                    "2009" -> 2009,
                                    "2010" -> 2010)
  val yearsOptions: List[(String, String)] = ("" -> "") :: years.keys
    .map(p => (p, p))
    .toList
  val yearsDefault = Empty
  // The function to call when an option is picked:
  def yearsHandler(selected: String): JsCmd = {
    this.year = years(selected)
    S.notice("Selected year '" + years(selected) + "'")
    SetHtml("selectedyear",
            <div>{"Selected year '" + years(selected) + "'"}</div>)
  }
  def nameHandler(name: String): JsCmd = {
    this.name = name
    S.notice("The name is '" + name + "'")
    SetHtml("namevalue", <div>{"The name is '" + name + "'"}</div>)
  }
  def submitHandler(): JsCmd = {
    S.notice(
      "The name and year was set to (" + this.name + "," + this.year + ")")
    S.redirectTo("#myForm")
  }
}
