package controllers

import play.api.mvc.{WebSocket, Action, Controller}
import play.api.libs.json.JsValue
import play.api.libs.concurrent.Akka
import akka.actor.Props
import models._
import play.api.libs.iteratee.{Iteratee, Concurrent}
import play.api.Play.current
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

object AppMain extends Controller {

  def welcome() = Action {
    Ok("simple message...")
  }



  def collactivityConnection() = WebSocket.using[JsValue] { request =>

    //Concurernt.broadcast returns (Enumerator, Concurrent.Channel)
      val (out,channel) = Concurrent.broadcast[JsValue]

      val userConnection = Akka.system.actorOf(Props(classOf[UserConnection], channel))

        //log the message to stdout and send response back to client
      val in = Iteratee.foreach[JsValue] {msg =>
        println(msg)
        userConnection ! MessageReceived(msg)
      }.map{ _ => userConnection ! Disconnect}

      (in,out)
    }


  def showHistory(contentId: String) = Action {
    val items = History.getHistoryFor(Location(Some(contentId)))

    Ok(views.html.Application.history(items))
  }

  def showFieldHistory(contentId: String, field: String) = Action {
    val items = History.getHistoryFor(Location(Some(contentId), Some(field)))

    Ok(views.html.Application.history(items))
  }
}