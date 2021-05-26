package controllers

import controllers.abstractTest
import dao.MovieDAO
import models.Movie
import scala.concurrent._
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.test._
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo.ReactiveMongoApi
import scala.util._
import play.api.libs.json._

class MovieControllerTest extends abstractTest {

  val mongo = mock[ReactiveMongoApi]
  val ec = mock[ExecutionContext]
  val dao = new MovieDAO()(ec, mongo)
  val controller = new MovieController(Helpers.stubControllerComponents(), mongo, dao)

  "A MovieController" can {

    "listMovies" should {
      "return a list of movies" in {
        controller.listMovies.apply(FakeRequest())
      }
    }
  }
}
