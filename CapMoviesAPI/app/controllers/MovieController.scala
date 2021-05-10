package controllers

import dao.MovieDAO
import models.JsonFormat.movieFormat
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.bson.BSONObjectID

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class MovieController @Inject()(components: ControllerComponents, val reactiveMongoApi: ReactiveMongoApi, dao: MovieDAO) extends AbstractController(components)
  with MongoController with ReactiveMongoComponents with play.api.i18n.I18nSupport {
  implicit def ec: ExecutionContext = components.executionContext

    def listMovies = Action async {
      dao.read(100).map { list =>
        Ok(Json.toJson(list))
      }
    }

    def read(id: BSONObjectID) = Action async {
      dao.readOne(id).map { movie =>
        movie.map { result =>
          Ok(Json.toJson((result)))
        }.getOrElse((NotFound))
      }
    }
}
