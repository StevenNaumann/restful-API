package app.repository

import java.sql.Connection

abstract class DataAccessObject[ResourceType, IDType]{
  val tableName: String = ???

  def read()(connection: Connection): Option[List[ResourceType]] = ???
  def readById(id: IDType)(connection: Connection): Option[ResourceType] = ???
  def readByQuery(values: Map[String, String])(connection: Connection): List[ResourceType] = ???
  def create()(connection: Connection): ResourceType = ???
  def update()(connection: Connection): ResourceType = ???
  def delete()(connection: Connection): ResourceType = ???
}
