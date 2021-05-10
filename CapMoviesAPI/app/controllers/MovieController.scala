package controllers

import dao.MovieDAO
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class MovieController @Inject()(components: ControllerComponents, val reactiveMongoApi: ReactiveMongoApi, dao: MovieDAO) extends AbstractController(components)
  with MongoController with ReactiveMongoComponents with play.api.i18n.I18nSupport {
  implicit def ec: ExecutionContext = components.executionContext

    def listMovies = Action async {
      dao.read(100).map { list =>
        Ok(views.html.movies(list))
      }
    }
}
