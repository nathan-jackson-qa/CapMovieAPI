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
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class MovieControllerTest extends abstractTest {

  val mongo = mock(classOf[ReactiveMongoApi])
  val dao = mock(classOf[MovieDAO])
  val controller = new MovieController(Helpers.stubControllerComponents(), mongo, dao)
  def await[T](arg: Future[T]): T = Await.result(arg, Duration.Inf)
  val movie = Movie(Option(BSONObjectID.parse("609a678ce1a52451685d793f").get), "Gladiator", "Ridley Scott", "Russell Crowe", "R", "Action", "images/posters/gladiator.jpg")
  val movie1 = Movie(Option(BSONObjectID.parse("609a678ce1a52451685d793f").get), "Gladiator", "Ridley Scott", "Russell Crowe", "R", "Action", "images/posters/gladiator.jpg")

    "ListMovies" should {
      "return a list of movies: Successful" in {
        when(dao.list()) thenReturn (Future.successful(Seq(movie, movie.copy(title = "NewTitle"))))

        val result: Future[Result] = controller.listMovies.apply(FakeRequest())

        await(result).header.status shouldBe (200)
        contentAsString(result) should include(movie.title)
        contentAsString(result) should include("NewTitle")

      }
      "return a list of movies: unSuccessful" in {
        when(dao.list()) thenReturn (Future.failed(new RuntimeException))

        val result: Future[Result] = controller.listMovies.apply(FakeRequest())

        await(result).header.status shouldBe (400)

      }
      "return a list of movies: empty" in {
        when(dao.list()) thenReturn (Future.successful(Seq()))

        val result: Future[Result] = controller.listMovies.apply(FakeRequest())

        await(result).header.status shouldBe (200)
        contentAsString(result) shouldNot include(movie.title)

      }
    }

    "filter" should {
      "return any movies with the matching genre" in {
        when(dao.filter("Action")) thenReturn (Future.successful(Seq(movie, movie.copy(title = "NewTitle"))))

        val result: Future[Result] = controller.filter("Action").apply(FakeRequest())

        await(result).header.status shouldBe (200)
        contentAsString(result) should include(movie.title)
        contentAsString(result) should include("NewTitle")
      }
      "return an empty list when no movies match the genre" in {
        when(dao.filter("Animation")) thenReturn (Future.successful(Seq()))
        val result: Future[Result] = controller.filter("Animation").apply(FakeRequest())

        await(result).header.status shouldBe (200)
        contentAsString(result) shouldNot include(movie.title)
      }
      "return a bad request when request is unsuccessful" in {
        when(dao.filter("Action")) thenReturn (Future.failed(new RuntimeException))
        val result: Future[Result] = controller.filter("Action").apply(FakeRequest())

        await(result).header.status shouldBe (400)

      }
    }

}
