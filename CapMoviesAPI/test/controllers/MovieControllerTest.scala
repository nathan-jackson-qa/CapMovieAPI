package controllers

import controllers.abstractTest
import dao.MovieDAO
import models.Movie
import org.mockito.ArgumentMatchers.{any, anyString}
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

    "ListMovies" should {
      "return a list of movies: Successful" in {
        when(dao.list()) thenReturn(Future.successful(Seq(movie, movie.copy(title = "NewTitle"))))

        val result: Future[Result] = controller.listMovies.apply(FakeRequest())

        await(result).header.status shouldBe(200)
        contentAsString(result) should include(movie.title)
        contentAsString(result) should include("NewTitle")
      }

      "return a list of movies: unSuccessful" in {
        when(dao.list()) thenReturn(Future.failed(new RuntimeException))

        val result: Future[Result] = controller.listMovies.apply(FakeRequest())

        await(result).header.status shouldBe(400)
      }

      "return a list of movies: empty" in {
        when(dao.list()) thenReturn(Future.successful(Seq()))

        val result: Future[Result] = controller.listMovies.apply(FakeRequest())

        await(result).header.status shouldBe(200)
        contentAsString(result) shouldNot include(movie.title)

      }
    }

  "searchMovies" should {
    "return a list of movies: successful" in {
      when(dao.search("Gladiator")) thenReturn Future.successful(Seq(movie, movie.copy(title = "NewTitle")))

      val result: Future[Result] = controller.search("Gladiator").apply(FakeRequest())

      await(result).header.status shouldBe(200)
      contentAsString(result) should include(movie.title)
      contentAsString(result) should include("NewTitle")
    }

    "return a list of movies: unsuccessful" in {
      when(dao.search("Gladiator")) thenReturn Future.failed(new RuntimeException)

      val result: Future[Result] = controller.search("Gladiator").apply(FakeRequest())

      await(result).header.status shouldBe(400)
    }

    "return a list of movies: empty" in {
      when(dao.search("Gladiator")) thenReturn Future.successful(Seq())

      val result: Future[Result] = controller.search("Gladiator").apply(FakeRequest())

      await(result).header.status shouldBe(200)
      contentAsString(result) shouldNot include(movie.title)
    }
  }


}
