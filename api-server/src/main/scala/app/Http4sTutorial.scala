package app

import app.Resources._
import app.repository.{AuthorDAO, MariaJDBC}
import cats._
import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import main.scala.app.repository.BookDAO
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.dsl.impl._
import org.http4s.headers._
import org.http4s.implicits._
import org.http4s.server._
import org.http4s.server.blaze.BlazeServerBuilder

object Http4sTutorial extends IOApp {

  // Request -> F[Response]
  // Request -> F[Option[Response]]
  // HttpRoutes[F]

  implicit val dbConnection: java.sql.Connection = MariaJDBC.getConnection()

  // Get /movies/author=Stephen%20King&year=1977
  def bookRoutes[F[_] : Concurrent]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F]{
      case GET -> Root / "books" =>
        BookDAO.read()(dbConnection) match {
          case Some(books) => Ok(books.asJson)
          case None => NotFound(s"No books found in the database.")
        }
      case GET -> Root / "books" / UUIDVar(bookId)  =>
        BookDAO.readById(bookId)(dbConnection) match {
          case Some(book) => Ok(book.asJson)
          case None => NotFound(s"No books with id $bookId found in the database.")
        }
    }
  }

  def authorRoutes[F[_]: Concurrent]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._
    implicit val authorDecoder: EntityDecoder[F, Author] = jsonOf[F, Author]

    HttpRoutes.of[F]{
      case GET -> Root / "authors" =>
        AuthorDAO.read()(dbConnection) match {
          case Some(authors) => Ok(authors.asJson)
          case None => NotFound(s"No authors found in the database.")
        }
      case GET -> Root / "authors" / UUIDVar(authorId) =>
        AuthorDAO.readById(authorId)(dbConnection) match{
          case Some(author) => Ok(author.asJson)
          case _ => NotFound(s"No Author '$authorId' found in database.")
        }
      case req@POST -> Root / "authors" =>
        for {
          author <- req.as[Author]
          _ = AuthorDAO.create(author)(dbConnection)
          response <- Ok.headers(`Content-Encoding`(ContentCoding.gzip))
            .map(_.addCookie(ResponseCookie("My-Cookie", "value")))
        } yield response
//      case PUT -> Root / "authors" / authorQueryParamMatcher(author) =>
//        AuthorDAO.update()(dbConnection) match{
//          case Some(author) => Ok(author.asJson)
//          case _ => NotFound(s"No Author '$authorId' found in database.")
//        }
      case DELETE -> Root / "authors" / UUIDVar(authorId) =>
        AuthorDAO.delete(authorId)(dbConnection) match{
          case Some(author) => Ok(author.asJson)
          case _ => NotFound(s"No Author '$authorId' found in database.")
        }
    }
  }

    def allRoutes[F[_] : Concurrent]: HttpRoutes[F] =
      bookRoutes[F] <+> authorRoutes[F] // cats.syntax.semigroupK._

    def allRoutesComplete[F[_]: Concurrent]: HttpApp[F] =
      allRoutes[F].orNotFound

  override def run(args: List[String]): IO[ExitCode] = {

    // Used with the logic of "allRoutesComplete" alternatively, you can use "apis" from the run method
    BlazeServerBuilder[IO](runtime.compute)
      .bindHttp(4000, "0.0.0.0")
      .withHttpApp(allRoutesComplete)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}
