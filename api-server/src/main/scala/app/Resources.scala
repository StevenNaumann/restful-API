package app

import java.sql.Date
import java.util.UUID

import io.circe.generic.auto._
import io.circe.syntax._

object Resources {
  // Library System
  case class Book(id: UUID, title: String, year: Int, genre: String)

  case class Author(firstName: String, lastName: String)
  case class Library(city: String, public: Boolean){
    override def toString = s"The $city Library is${if(!public) " not" else ""} public."
  }
  case class AuthorDetails(firstName: String, lastName: String, genre: String)
}
