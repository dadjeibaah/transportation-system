package models

import akka.util.ByteString

import play.api.libs.json._
import play.api.libs.functional.syntax._
// Combinator syntax

/**
  * Created by dennis on 6/18/17.
  */
case class LineMessage(messageId: String, lineName: String, stationName: String, busId: Long)
trait LineMessageDeserializer {
  def convertFromByteString(message: ByteString): LineMessage = Json.parse(message.decodeString("UTF-8")).validate[LineMessage] match {
    case s: JsSuccess[LineMessage] => s.value
    case _: JsError => LineMessage("", "", "", 1)
  }
}

object LineMessage {
  implicit val LineMessageReads: Reads[LineMessage] = (
    (JsPath \ "messageId").read[String] and
      (JsPath \ "message" \ "lineName").read[String] and
      (JsPath \ "message" \ "stationName").read[String] and
      (JsPath \ "message" \ "busId").read[Long]
    ) (LineMessage.apply _)

}