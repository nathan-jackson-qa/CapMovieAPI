package controllers

import controllers.abstractTest
import dao.MovieDAO
import models.Movie
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.Mockito._
import org.scalatestplus.play.guice._
import play.api.Play.materializer

import scala.concurrent._
import play.api.test._
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo.ReactiveMongoApi

import scala.util._
import play.api.libs.json._
import play.api.mvc.Result
import play.api.test.Helpers.{status, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class MovieControllerTest extends abstractTest with GuiceOneAppPerSuite {

  val mongo = mock(classOf[ReactiveMongoApi])
  val dao = mock(classOf[MovieDAO])
  val controller = new MovieController(Helpers.stubControllerComponents(), mongo, dao)
  def await[T](arg: Future[T]): T = Await.result(arg, Duration.Inf)
  val movie = Movie(Option(BSONObjectID.parse("609a678ce1a52451685d793f").get), "Gladiator", "Ridley Scott", "Russell Crowe", "R", "Action", "images/posters/gladiator.jpg")


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

  "read" should {
    "return a movie" in {
      when(dao.read(BSONObjectID.parse("609a678ce1a52451685d793f").get)) thenReturn(Future.successful(Some(movie)))

      val result: Future[Result] = controller.read(BSONObjectID.parse("609a678ce1a52451685d793f").get).apply(FakeRequest())

      await(result).header.status shouldBe(200)

    }
    "not return a movie if not found" in {
      when(dao.read(any())) thenReturn(Future.successful(None))

      val result: Future[Result] = controller.read(any()).apply(FakeRequest())

      await(result).header.status shouldBe(404)

    }
  }

  "update" should {
    "takes in no parameters and returns a error" in {
      when(dao.update(any(), any())) thenReturn(Future.successful(Some(movie)))
      val result = controller.update(BSONObjectID.parse("609a678ce1a52451685d793f").get)(FakeRequest().withHeaders("Content-Type" -> "application/json"))
      status(result) shouldBe(400)
    }
    "takes in parameters and updates the movie" in {
      when(dao.update(any(), any())) thenReturn(Future.successful(Some(movie)))
      val js = Json.parse("""{
                            |	"title": "Gladiator",
                            |	"director": "Ridley Scott",
                            |	"actors": "Russell Crowe",
                            |	"rating": "R",
                            |	"genre": "Action",
                            |	"img": "images/posters/gladiator.jpg"
                            |}""".stripMargin
      )
      val result = controller.update(BSONObjectID.parse("609a678ce1a52451685d793f").get)(FakeRequest("PUT", "/update", FakeHeaders(Seq("Content-Type" -> "application/json")), js))
      status(result) shouldBe(200)
    }
  }
}
