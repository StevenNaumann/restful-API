package app.routes

import app.Main.dbConnection
import cats.effect.Concurrent
import io.circe.syntax.EncoderOps
import main.scala.app.repository.BookDAO
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import io.circe.generic.auto._
import org.http4s.circe._

object Books{

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


  }
}
