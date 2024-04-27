package app

import app.Resources._
import app.repository.MariaJDBC
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

import java.time.Year
import scala.collection.mutable
import scala.util.Try

object Http4sTutorial extends IOApp {

  /*
    - GET all books for a Author published in a given year
    - GET all Authors for a Book
    - GET details about author
    - POST add a new author
    - POST add a book
   */

  // Request -> F[Response]
  // Request -> F[Option[Response]]
  // HttpRoutes[F]

  implicit val dbConnection: java.sql.Connection = MariaJDBC.getConnection()

  implicit val authorQueryParamDecoder: QueryParamDecoder[Author] =
    QueryParamDecoder[String].map{ author =>
      val authorNames = author.split(" ")
      Author(authorNames(0), authorNames(1))
    }
  object AuthorQueryParamMatcher extends QueryParamDecoderMatcher[Author]("author")(authorQueryParamDecoder)

  // Get /movies/author=Stephen%20King&year=1977
  def bookRoutes[F[_] : Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F]{
//      case GET -> Root / "books" :? AuthorQueryParamMatcher(author) +& YearQueryParamMatcher(maybeYear) =>
//        val booksByAuthor = Resources.findBooksByAuthor(author)
//        maybeYear match {
//          case Some(validatedYear) =>
//            validatedYear.fold(
//              _ => BadRequest("The year was badly formatted"),
//              year => {
//                val booksByAuthorAndYear = booksByAuthor.filter(_.year == year.getValue)
//                Ok(booksByAuthorAndYear.asJson)
//              })
//          case None => Ok(booksByAuthor.asJson)
//        }
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

  object AuthorPath {
    def unapply(str: String): Option[Author] = {
      Try {
        val tokens = str.split(" ")
        Author(tokens(0), tokens(1))
      }
    }.toOption
  }

  val authorDetailsDB: mutable.Map[Author, AuthorDetails] =
    mutable.Map((Author("Stephen", "King"), AuthorDetails("Stephen", "King", "horror")))

  def authorRoutes[F[_]: Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F]{
      case GET -> Root / "authors" / AuthorPath(author) =>
        authorDetailsDB.get(author) match{
          case Some(authorDetails: AuthorDetails) => Ok(authorDetails.asJson)
          case _ => NotFound(s"No Author '$author' found in database.")
        }
    }
  }

  def allRoutes[F[_] : Monad]: HttpRoutes[F] =
    bookRoutes[F] <+> authorRoutes[F] // cats.syntax.semigroupK._

  def allRoutesComplete[F[_]: Monad]: HttpApp[F] =
    allRoutes[F].orNotFound

  override def run(args: List[String]): IO[ExitCode] = {
    val apis = Router(
      "/api" -> bookRoutes[IO],
      "/api/admin" -> authorRoutes[IO]
    ).orNotFound

    // Used with the logic of "allRoutesComplete" alternatively, you can use "apis" from the run method
    BlazeServerBuilder[IO](runtime.compute)
      .bindHttp(4000, "0.0.0.0")
      .withHttpApp(allRoutesComplete)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}
