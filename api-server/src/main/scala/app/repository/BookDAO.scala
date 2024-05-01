package main.scala.app.repository

import app.Resources._
import app.repository.{AuthorDAO, DataAccessObject}
import app.repository.MariaJDBC.ImprovedResult

import java.sql.{Connection, ResultSet}
import java.util.UUID
object BookDAO extends DataAccessObject[Book, UUID]{
  override val tableName = "books"

  def getBook(result: ResultSet): Book = {
    Book(
      result.getString("title"),
      result.getInt("publish_date"),
      result.getString("genre"),
      UUID.fromString(result.getString("book_id")))
  }
  override def read()(dbConnection: Connection): Option[List[Book]] = {
    val statement = dbConnection.createStatement()
    val resultSet = statement.executeQuery(s"""SELECT * FROM $tableName""")

    Some(resultSet.map{result =>
      getBook(result)
    })
  }

  override def readById(id: UUID)(dbConnection: Connection): Option[Book] = {
    val statement = dbConnection.createStatement()
    val resultSet = statement.executeQuery(s"""SELECT * FROM $tableName where book_id = $id""")

    if(resultSet.next()){
      Some(getBook(resultSet))
    } else {
      None
    }
  }

  def findBooksByAuthor(author: Author)(dbConnection: Connection) : List[Book] = {
    val statement = dbConnection.createStatement()
    val resultSet = statement.executeQuery(
      s"""SELECT book_id, title, publish_date, genre FROM $tableName as book
         | INNER JOIN book_authors ON
         | book_authors.book_id = $tableName.book_id
         | INNER JOIN ${AuthorDAO.tableName} AS author ON
         | book_authors.author_id = author.author_id
         | WHERE author.first_name = '${author.firstName}' AND
         | author.last_name = '${author.lastName}'
         |""".stripMargin)

    resultSet.map{result =>
      getBook(result)
    }
  }

  override def create(book: Book)(dbConnection: Connection): Option[Book] = ???
  override def update(id: UUID, book: Book)(dbConnection: Connection): Option[Book] = ???
  override def delete(id: UUID)(dbConnection: Connection): Option[Book] = ???
}
