/*
 * Copyright 2008-2010 WorldWide Conferencing, LLC
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

/*
 This "script" will recursively search a given directory for any scala, xhtml, or html
 files and will search each file found for any "<lift:loc/>" tags or calls to S.?(...).
 It outputs a list of the localization keys found.
 */

import _root_.java.io.{File,FileFilter,FilenameFilter}


import scala.io.Source
import scala.xml.XML

object LocKeyGrabber {
  // Helper function to pull out keys from an XML (xhtml, html, etc) file
  def parseXML (input : File) : Seq[String] =
    try {
      (XML.loadFile(input) \\ "loc").filter(node => node.prefix == "lift").map(node => node.attribute("locid") match {
	case Some(locid) => locid.toString.replace(" ","\\ ") + "=" // Just return the key
	case None => node.child.mkString("","","=").replace(" ","\\ ")
      })
    } catch {
      case e : Exception => { format("Error parsing %s : %s", input, e.getMessage); exit(1) }
    }

  // Helper function to pull keys out of a Lift scala file. Pretty naieve, but until I figure out
  // how to hook into the compiler/AST this isn't going to be pretty or clever
  val scalaRegex = """\?\s*\(\s*\"([^\"]*)\"\s*\)""".r
  def parseScala (input : File) : Seq[String] =
    Source.fromFile(input).getLines.flatMap({line => scalaRegex.findAllIn(line).matchData.map(grp => grp.group(0).replace(" ","\\ ") + "=")}).toList

  // Define filters for scanning directories
  val scalaFileFilter = new FilenameFilter() {
    override def accept(parent : File, name : String) = name.toLowerCase.endsWith(".scala")
  }

  val xmlFileFilter = new FilenameFilter() {
    override def accept(parent : File, name : String) = {
      val ln = name.toLowerCase
      (ln != "web.xml") && (ln.endsWith(".xml") || ln.endsWith(".html") || ln.endsWith(".xhtml"))
    }
  }

  val directoryFilter = new FileFilter () {
    override def accept(file : File) = file.isDirectory
  }

  def scanDir(dir : File) : Seq[String] = {
    val scalaKeys = dir.listFiles(scalaFileFilter).flatMap(parseScala)
    val xmlKeys = dir.listFiles(xmlFileFilter).flatMap(parseXML)
    val dirKeys = dir.listFiles(directoryFilter).flatMap(scanDir)

    scalaKeys ++ xmlKeys ++ dirKeys
  }

  def main (args : Array[String]) = args match {
    case Array(input) => scanDir(new File(input)).foldLeft(Set[String]())((set,key) => set + key).toList.sort(_ < _).foreach(println)
    case _ => println("Usage: LocKeyGrabber <input file>")
  }
}




