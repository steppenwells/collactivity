package models

import play.api.libs.concurrent.Akka
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import akka.actor.ActorRef
import play.api.libs.json.Json

object ActiveConnections {

  var connections: List[Connection] = Nil

  // occurs on connection, not at field level
  def connected(c: Connection) {
    val connectionsToTell = connections.filter{conn =>
      conn.location.contentId == None || conn.location.contentId == c.location.contentId
    }

    val connectionsToBeToldAbout = connections.filter(c.location.contentId == None || _.location.contentId == c.location.contentId)

    connections = c :: connections.filterNot(_.id == c.id)

    connectionsToTell foreach { conn =>
      conn.socketActor ! c.toUserAtMessage
    }

    connectionsToBeToldAbout foreach { conn =>
      c.socketActor ! conn.toUserAtMessage
    }
  }

  def enteredContent(c: Connection) {
    val connectionsToTell = connections.filter{conn =>
      conn.location.contentId == None || conn.location.contentId == c.location.contentId
    }

    val connectionsToBeToldAbout = connections.filter(_.location.contentId == c.location.contentId)

    connections = c :: connections.filterNot(_.id == c.id)

    connectionsToTell foreach { conn =>
      conn.socketActor ! c.toUserAtMessage
    }

    connectionsToBeToldAbout foreach { conn =>
      c.socketActor ! conn.toUserAtMessage
    }
  }

  def leftContent(c: Connection) {
    val connectionsToDisconnect = connections.filter{conn =>
      conn.location.contentId == c.location.contentId
    }.filterNot(_.id == c.id)

    val connectionsToConnect = connections.filter {conn => conn.location.contentId == None}

    val connectionsToBeToldAbout = connections.filterNot(_.id == c.id)
    val updatedConnection = c.copy(location = new Location())

    connections = updatedConnection :: connections.filterNot(_.id == c.id)

    connectionsToDisconnect foreach { conn =>
      conn.socketActor ! c.toUserLeftMessage
    }

    connectionsToConnect foreach { conn =>
      conn.socketActor ! updatedConnection.toUserAtMessage
    }

    connectionsToBeToldAbout foreach { conn =>
      c.socketActor ! conn.toUserAtMessage
    }
  }

  // occurs on content field level
  def enteredField(c: Connection) {
    val connectionsToTell = connections.filter{conn =>
      conn.location.contentId == c.location.contentId
    }

    connections = c :: connections.filterNot(_.id == c.id)

    connectionsToTell foreach { conn =>
      conn.socketActor ! c.toUserAtMessage
    }
  }

  // occurs on content field level
  def leftField(c: Connection, value: Option[String]) {

    History.record(c, value)

    val connectionsToTell = connections.filter{conn =>
      conn.location.contentId == c.location.contentId
    }

    val updatedConnection = c.copy(location = c.location.copy(field = None))
    connections = updatedConnection :: connections.filterNot(_.id == c.id)

    connectionsToTell foreach { conn =>
      conn.socketActor ! FieldUpdated(c.id, c.user, c.location, value)
    }
  }

  def disconnect(id: String) {
    val currentConnection = connections.filter(_.id == id).headOption
    currentConnection foreach { c =>
      val connectionsToTell = connections.filter{conn =>
        conn.location.contentId == None || conn.location.contentId == c.location.contentId
      }

      connectionsToTell foreach( _.socketActor ! c.toUserLeftMessage)
    }

    connections = connections.filterNot(_.id == id)
  }

}

case class Connection(id: String, socketActor: ActorRef, user: User, location: Location) {
  def toUserAtMessage = UserConnected(id, user, location)

  def toUserLeftMessage = UserDisconnected(id, user, location)
}




