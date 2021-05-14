package dao

import dao.MovieDAOTest
import models.Movie
import org.mockito.Mockito.when
import org.mockito._
import org.scalatestplus.mockito.MockitoSugar.mock
import play.libs.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.BSONObjectID

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MovieDAOTest extends abstractTest {
  val mongo = mock[ReactiveMongoApi]
  val ec = mock[ExecutionContext]
  val dao = new MovieDAO()(ec, mongo)

  "A DAO" can {
    "calling read" should {
      "pls" in {
        val id = BSONObjectID.generate()
        val result = dao.readOne(id)
        val movie = mock[Future[Option[Movie]]]
        when(dao.readOne(id)).thenReturn(movie)
        assert(dao.readOne(id) == movie)
      }
    }
  }
}
