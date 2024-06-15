package app.routes

import app.Main.dbConnection
import app.Resources.Author
import app.repository.AuthorDAO
import cats.effect.Concurrent
import io.circe.syntax.EncoderOps
import org.http4s.circe.jsonOf
import org.http4s.{ContentCoding, EntityDecoder, HttpRoutes, ResponseCookie}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Content-Encoding`
import io.circe.generic.auto._
import cats.implicits._
import org.http4s.circe._


object Author {
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
