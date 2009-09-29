package bootstrap.liftweb

import _root_.net.liftweb.base._
import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._

import _root_.net.liftweb.http.auth._

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
    LiftRules.addToPackages("net.liftweb.examples.authentication")

    /**
     * This is the security function.
     * The URL's specified in protectedResource are secured by
     * this scheme.
     */
     LiftRules.httpAuthProtectedResource.prepend {
       case (ParsePath("secure-basic" :: Nil, _, _, _)) => Full(AuthRole("admin"))
     }

     LiftRules.authentication = HttpBasicAuthentication("lift") {
       case ("someuser", "1234", req) => {
         Log.info("You are now authenticated !")
         userRoles(AuthRole("admin"))
         true
       }
     }

     /**
      * A more complex example of nested roles and authorization
      */
     /*

     val roles = AuthRole("admin").addRoles(
       AuthRole("siteadmin"),
       AuthRole("useradmin").addRoles(
         AuthRole("romania-admin"),
         AuthRole("uk-admin")
         )
       )

     LiftRules.httpAuthProtectedResource.append {
       case (ParsePath("index" :: _, _, _, _)) => roles.getRoleByName("siteadmin")
     }

     LiftRules.authentication = HttpDigestAuthentication("lift") {
       case ("administrator", req, func) => if (func("verfunc")) {
         userRoles(AuthRole("admin"))
         true
       } else {
         println("Not verified")
         false
       }
     }

     */
  }
}

