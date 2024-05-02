package app.repository

import app.Resources._
import app.repository.MariaJDBC.ImprovedResult

import java.sql.{Connection, ResultSet}
import java.util.UUID

object AuthorDAO extends DataAccessObject[Author, UUID] {
  override val tableName = "authors"

  def getAuthor(result: ResultSet): Author = {
    Author(
      result.getString("first_name"),
      result.getString("last_name"),
      UUID.fromString(result.getString("author_id")))
  }

  override def read()(dbConnection: Connection): Option[List[Author]] = {
    val statement = dbConnection.createStatement()
    val resultSet = statement.executeQuery(s"""SELECT * FROM $tableName""")

    Some(resultSet.map { result =>
      getAuthor(result)
    })
  }

  override def readById(id: UUID)(dbConnection: Connection): Option[Author] = {
    val statement = dbConnection.createStatement()
    val resultSet = statement.executeQuery(s"""SELECT * FROM $tableName where author_id = $id""")

    if (resultSet.next()) {
      Some(getAuthor(resultSet))
    } else {
      None
    }

  }

  override def create(author: Author)(dbConnection: Connection): Option[Author] = {
    val statement = dbConnection.createStatement()
    val resultSet = statement.execute(
      s"""INSERT INTO  $tableName
         |(first_name, last_name, author_id)
         |VALUES
         |("${author.firstName}", "${author.lastName}", "${author.id}");""".stripMargin)

    None
  }
  override def update(id: UUID, author: Author)(dbConnection: Connection): Option[Author] = ???
  override def delete(id: UUID)(dbConnection: Connection): Option[Author] = ???

}
