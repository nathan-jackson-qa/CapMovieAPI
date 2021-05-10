package controllers

import dao.MovieDAO
import models.JsonFormat.movieFormat
import models.Movie
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.bson.{BSONDocument, BSONObjectID}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MovieController @Inject()(components: ControllerComponents, val reactiveMongoApi: ReactiveMongoApi, dao: MovieDAO) extends AbstractController(components)
  with MongoController with ReactiveMongoComponents with play.api.i18n.I18nSupport {
  implicit def ec: ExecutionContext = components.executionContext

    def listMovies = Action async {
      dao.read(100).map { list =>
        Ok(views.html.movies(list))
      }
    }

  def update(id: BSONObjectID) = Action.async(parse.json) {
    _.body.validate[Movie].map { result =>
      dao.update(id, result).map {
        case Some(feed) =>Ok(Json.toJson(feed))
        case _ => NotFound
      }
    }.getOrElse(Future.successful(BadRequest("Invalid update")))
  }
}
