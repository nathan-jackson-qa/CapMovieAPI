package dao

import models.JsonFormat.movieFormat
import models.Movie
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MovieDAO @Inject()(implicit ec: ExecutionContext, reactiveMongoApi: ReactiveMongoApi){

  private def collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("Movies"))

  def read( limit: Int = 100): Future[Seq[Movie]] =
      collection.flatMap(
      _.find(BSONDocument())
      .cursor[Movie](ReadPreference.Primary)
      .collect[Seq](limit, Cursor.FailOnError[Seq[Movie]]()))

}
