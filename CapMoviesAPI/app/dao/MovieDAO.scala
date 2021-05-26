package dao

import models.Movie
import play.api.libs.json.{JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MovieDAO @Inject()(implicit ec: ExecutionContext, reactiveMongoApi: ReactiveMongoApi) {

  private def collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("Movies"))

  def list(limit: Int = 100): Future[Seq[Movie]] =
    collection.flatMap(
      _.find(BSONDocument())
        .cursor[Movie](ReadPreference.Primary)
        .collect[Seq](limit, Cursor.FailOnError[Seq[Movie]]()))


  def update(id: BSONObjectID, movie: Movie): Future[Option[Movie]] = {
    collection.flatMap(_.findAndUpdate(BSONDocument("_id" -> id), BSONDocument(f"$$set" -> BSONDocument(
      "title" -> movie.title,
      "director" -> movie.director,
      "actors" -> movie.actors,
      "rating" -> movie.rating,
      "genre" -> movie.genre,
      "img" -> movie.img
    )
    ), true)
      .map(_.result[Movie]))
  }

  def read(id: BSONObjectID): Future[Option[Movie]] =
    collection.flatMap((_.find(BSONDocument("_id" -> id)).one[Movie]))

  def delete(id: BSONObjectID): Future[Option[Movie]] =
    collection.flatMap(_.findAndRemove(BSONDocument("_id" -> id)).map(_.result[Movie]))

  def create(movie: Movie): Future[WriteResult] =
    collection.flatMap(_.insert(movie))

  def search(searchTerm: String, limit: Int = 100) = {
    collection.flatMap(
      _.find(BSONDocument("$text" -> BSONDocument("$search" -> searchTerm)))
        .cursor[Movie](ReadPreference.Primary)
        .collect[Seq](limit, Cursor.FailOnError[Seq[Movie]]())
    )
  }

  def filter(genre: String): Future[Seq[Movie]] = {
    collection.flatMap(_.find(Json.obj("genre" -> genre))
      .cursor[Movie](ReadPreference.Primary)
      .collect[Seq](100, Cursor.FailOnError[Seq[Movie]]()))
  }
}
