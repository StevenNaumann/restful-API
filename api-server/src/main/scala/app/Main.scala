package app

import app.repository.MariaJDBC
import app.routes.Books._
import app.routes.Author._
import cats.effect._
import cats.implicits._
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

object Main extends IOApp {

  implicit val dbConnection: java.sql.Connection = MariaJDBC.getConnection()
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
