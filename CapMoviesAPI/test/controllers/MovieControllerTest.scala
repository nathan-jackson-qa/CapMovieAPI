package controllers

import controllers.abstractTest
import dao.MovieDAO
import models.Movie
import org.mockito.Mockito._

import scala.concurrent._
import play.api.test._
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo.ReactiveMongoApi

import scala.util._
import play.api.libs.json._
import play.api.mvc.Result

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class MovieControllerTest extends abstractTest {

  val mongo = mock(classOf[ReactiveMongoApi])
  val dao = mock(classOf[MovieDAO])
  val controller = new MovieController(Helpers.stubControllerComponents(), mongo, dao)
  def await[T](arg: Future[T]): T = Await.result(arg, Duration.Inf)
  val movie = Movie(Option(BSONObjectID.parse("609a678ce1a52451685d793f").get), "Gladiator", "Ridley Scott", "Russell Crowe", "R", "Action", "images/posters/gladiator.jpg")


  "MovieController" can {
    "ListMovies" should {
      "return a list of movies" in {
        when(dao.list()) thenReturn(Future.successful(Seq(movie)))

        val result: Result = await(controller.listMovies.apply(FakeRequest()))

        result.header.status shouldBe(200)

      }
    }
  }


}
