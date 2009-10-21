package bootstrap.liftweb

/*                                                *\
  (c) 2007-2009 WorldWide Conferencing, LLC
  Distributed under an Apache License
  http://www.apache.org/licenses/LICENSE-2.0
\*                                                 */

import _root_.net.liftweb.util.{Helpers, Log, NamedPF}
import _root_.net.liftweb.common.{Box, Empty, Full, Failure}
import _root_.net.liftweb.http._
import _root_.net.liftweb.mapper._
import Helpers._
import _root_.net.liftweb.mapper.{DB, ConnectionManager, Schemifier, DefaultConnectionIdentifier, ConnectionIdentifier}
import _root_.java.sql.{Connection, DriverManager}
import _root_.com.skittr.model._
import _root_.com.skittr.actor._
import provider._

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def modelList = List[BaseMetaMapper](User, Friend, MsgStore)
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)
    LiftRules.addToPackages("com.skittr")

    // make sure the database is up to date
    Schemifier.schemify(true, Log.infoF _, modelList :_*)

    if ((System.getProperty("create_users") != null) && User.count < User.createdCount) User.createTestUsers

    // map certain urls to the right place
    val rewriter: LiftRules.RewritePF = NamedPF("User and Friend mapping") {
    case RewriteRequest(ParsePath("user" :: user :: _, _, _,_), _, _) =>
       RewriteResponse("user" :: Nil, Map("user" -> user))
    case RewriteRequest(ParsePath("friend" :: user :: _, _, _,_), _, _) =>
       RewriteResponse("friend" :: Nil, Map("user" -> user))
    case RewriteRequest(ParsePath("unfriend" :: user :: _, _, _, _), _, _) =>
       RewriteResponse("unfriend" :: Nil, Map("user" -> user))
  }

  LiftRules.rewrite.prepend(rewriter)

  // load up the list of user actors
  UserList.create
  }
}

/**
  * A singleton that vends a database connection to a Derby database
  */
object DBVendor extends ConnectionManager {
  def newConnection(name: ConnectionIdentifier): Box[Connection] = {
    try {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver")
      val dm =  DriverManager.getConnection("jdbc:derby:skittr;create=true")

      Full(dm)
    } catch {
      case e : Exception => e.printStackTrace; Empty
    }
  }
  def releaseConnection(conn: Connection) {conn.close}
}
