package app

import io.circe.generic.auto._
import io.circe.syntax._

import java.util.UUID

object Resources {
  // Library System
  case class Book(title: String, year: Int, genre: String, id: UUID = UUID.randomUUID())

  case class Author(firstName: String, lastName: String, id: UUID = UUID.randomUUID())

  case class Library(city: String, public: Boolean){
    override def toString = s"The $city Library is${if(!public) " not" else ""} public."
  }
}
