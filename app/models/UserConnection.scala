package models

import play.api.libs.iteratee.Concurrent.Channel
import akka.actor.{ActorRef, Actor}
import play.api.libs.json.{Json, JsValue}
import MessageModel._
import java.util.UUID


class UserConnection (channel: Channel[JsValue]) extends Actor {

  val id = UUID.randomUUID.toString
    //var currentGame: Option[ActorRef] = None

  def receive = {

    case MessageReceived(json) => {
      val event = json.as[Event]
      event.activity match {
        case "connected" => ActiveConnections.connected(new Connection(id, self, event.user, event.location))
        case "enteredField" => ActiveConnections.enteredField(new Connection(id, self, event.user, event.location))
        case "leftField" => ActiveConnections.leftField(new Connection(id, self, event.user, event.location), event.value)
      }

    }

    case Disconnect => {
      ActiveConnections.disconnect(id)
      context.stop(self)
    }

    case uc: UserConnected => {
      sendUpdate(uc.asJson)
    }

    case ud: UserDisconnected => {
      sendUpdate(ud.asJson)
    }

    case fe: FieldUpdated => {
      sendUpdate(fe.asJson)
    }
  }

  def sendUpdate(json: JsValue) {
    try {
      channel.push(json)
    } catch {
      case e :Exception => println(s"\t\terror sending update: $e")
    }
  }

  
}

// from websocket
case object Disconnect
case class MessageReceived(json: JsValue)

// to websocket
case class UserConnected(id: String, user: User, location: Location) {
  def asJson = Json.obj("event" -> "userConnected", "id" -> id, "user" -> user.asJson, "location" -> location.asJson)
}

case class UserDisconnected(id: String, user: User, location: Location) {
  def asJson = Json.obj("event" -> "userDisconnected", "id" -> id, "user" -> user.asJson, "location" -> location.asJson)
}

case class FieldUpdated(id: String, user: User, location: Location, value: Option[String]){
  def asJson = {
    val valString = value.getOrElse("")
    Json.obj("event" -> "fieldUpdated", "id" -> id, "user" -> user.asJson, "location" -> location.asJson, "value" -> valString)
  }
}
