package fbc.example.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import scala.xml.Text

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users" // define the DB table name
  override def screenWrap = Full(<lift:surround with="default" at="content">
			       <lift:bind /></lift:surround>)
  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, firstName, lastName, email,
  locale, timezone, password)

  // comment this line out to require email validations
  override def skipEmailValidation = true
  
  def findByFbId(fbid: Long):Box[User] = find(By(User.fbid, fbid))
  def findByFbId(fbid: String):Box[User] = findByFbId(fbid.toLong)
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a facebook id.
 */
class User extends MegaProtoUser[User] {
  def getSingleton = User // what's the "meta" server

  def validateUnique(field: MappedLong[User], msg: => String)(value:Long): List[FieldError] = value match {
    case 0 => Nil
    case _ => User.findAll(By(field,value)).filter(!_.comparePrimaryKeys(field.fieldOwner)) match {
        case Nil => Nil
        case x :: _ => 
          field.set(0)
          List(FieldError(field, Text(msg)))
      }
  }

  object fbid extends MappedLong(this) {
    override def dbIndexed_? = true
    override def dbNotNull_? = true
    override def validations = validateUnique(this, "Whoa there, this facebook account is already in use!") _ :: super.validations
  }
}
