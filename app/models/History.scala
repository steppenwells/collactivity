package models

import org.joda.time.DateTime


object History {

  var historyItems: List[HistoryItem] = Nil

  def record(c: Connection, value: Option[String]) {

    val lastValue = getMostRecentHistoryFor(c.location).flatMap(_.value)
    if(lastValue != value) {
      historyItems = HistoryItem(new DateTime(), c.location, c.user, value) :: historyItems
    }
  }

  def getMostRecentHistoryFor(l: Location) = {
    historyItems.filter(_.location == l).headOption
  }

  def getHistoryFor(l: Location) = {
    val contentHistory = historyItems.reverse.filter(_.location.contentId == l.contentId)

    if(l.field.isDefined) {
      contentHistory.filter(_.location.field == l.field)
    } else {
      contentHistory
    }
  }

}

case class HistoryItem(when: DateTime, location: Location, user: User, value: Option[String])
