package models

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class User(name: String, email: String) {
  def asJson = Json.obj("name" -> name, "email" -> email)
}

case class Location(contentId: Option[String] = None, field: Option[String] = None) {
  def asJson = Json.obj("contentId" -> contentId, "field" -> field)
}

case class Event(activity: String, user: User, location: Location, value: Option[String])

object MessageModel {

  implicit val userReads: Reads[User] = (
    (JsPath \ "name").read[String] and
    (JsPath \ "email").read[String]
  )(User.apply _)

  implicit val userWrites: Writes[User] = (
    (JsPath \ "name").write[String] and
    (JsPath \ "email").write[String]
  )(unlift(User.unapply))


  implicit val locationReads: Reads[Location] = (
    (JsPath \ "contentId").readNullable[String] and
    (JsPath \ "field").readNullable[String]
  )(Location.apply _)

  implicit val locationWrites: Writes[Location] = (
    (JsPath \ "contentId").writeNullable[String] and
    (JsPath \ "field").writeNullable[String]
  )(unlift(Location.unapply))


  implicit val eventReads: Reads[Event] = (
    (JsPath \ "activity").read[String] and
    (JsPath \ "user").read[User] and
    (JsPath \ "location").read[Location] and
    (JsPath \ "value").readNullable[String]
  )(Event.apply _)

  implicit val eventWrites: Writes[Event] = (
    (JsPath \ "activity").write[String] and
    (JsPath \ "user").write[User] and
    (JsPath \ "location").write[Location] and
    (JsPath \ "value").writeNullable[String]
  )(unlift(Event.unapply))
}
