package main.scala.app.repository

import app.Resources._
import app.repository.DataAccessObject
import app.repository.MariaJDBC.ImprovedResult

import java.sql.{Connection, ResultSet}
import java.util.UUID
object BookDAO extends DataAccessObject[Book, UUID]{
  override val tableName = "books"

  def getBook(result: ResultSet): Book = {
    Book(result.getString("book_id").asInstanceOf[UUID],
    result.getString("title"),
    result.getInt("publish_date"),
    result.getString("genre"))
  }
  override def read()(dbConnection: Connection): Option[List[Book]] = {
    val statement = dbConnection.createStatement()
    val resultSet = statement.executeQuery(s"""SELECT * FROM $tableName""")

    Some(resultSet.map{result =>
      getBook(result)
    })
  }

  def readById(id: String)(dbConnection: Connection): Option[Book] = {
    val statement = dbConnection.createStatement()
    val resultSet = statement.executeQuery(s"""SELECT * FROM $tableName where book_id = $id""")

    if(resultSet.next()){
      Some(getBook(resultSet))
    } else {
      None
    }

  }

//  def findBooksByAuthor(author: Author)(dbConnection: Connection) : List[Book] = {
//    val statement = dbConnection.createStatement()
//    val resultSet = statement.executeQuery(s"""SELECT * FROM $tableName where """)
//
//    resultSet.map{result =>
//      Book(result.getString("book_id"),
//        result.getString("title"),
//        result.getDate("publish_date"),
//        result.getString("genre")
//      )
//    }
//  }
}
