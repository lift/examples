package net.liftweb.example.snippet

import java.text.SimpleDateFormat
import java.util.Date

import net.liftweb.util.Helpers._
import net.liftweb.util._

import net.liftweb.example.lib.BuildInfo

class CacheBust {
  private lazy val date: Date = new Date(BuildInfo.buildTime)
  private lazy val formatter = new SimpleDateFormat("yyMMddHHmmss")
  private lazy val strDate: String = formatter.format(date)

  def usingBuildTime: CssSel =
    "link [href+]" #> s"?bt=$strDate" & "script [src+]" #> s"?bt=$strDate"
}
