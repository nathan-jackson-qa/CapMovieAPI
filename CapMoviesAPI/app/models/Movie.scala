package models

import play.api.data.Form
import play.api.data.Forms.{boolean, default, mapping, number, of, text}
import play.api.data.format.Formats.doubleFormat
import play.api.libs.json.{Json, OFormat}
import play.api.libs.json.__
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._


case class Movie(_id: Option[BSONObjectID], title: String, director: String, actors: String, rating: String, genre: String, img: String)

object JsonFormat {
  // Generates Writes and Reads for Feed and User thanks to Json Macros
  implicit val movieFormat: OFormat[Movie] = Json.format[Movie]
}

