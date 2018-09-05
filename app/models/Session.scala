package models

import play.api.libs.json.Json

case class Session(id: String, teamName: String, numberOfParticipants: Int)

object Session {
  implicit val formats = Json.format[Session]
}
