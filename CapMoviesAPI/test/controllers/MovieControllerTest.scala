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

//  "A Controller" can {
//  "read one move so it" should {
//    "return a movie" in {
//      val id = BSONObjectID.parse("609d06ef5fa85241e86c22e6")
//      id match {
//        case Success(id) => {
//          controller.read(id)
//        }
//        case Failure(_) => assert(false)
//      }
//    }
//  }
//}

  "A controller" can {
    "call the list fucntion" should {
      "return a list of movies" in {
        controller.listMovies.apply(FakeRequest())
      }
    }
  }
}
