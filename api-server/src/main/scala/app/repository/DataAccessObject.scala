package app.repository

import java.sql.Connection

abstract class DataAccessObject[ResourceType, IDType]{
  val tableName: String

  def read()(connection: Connection): Option[List[ResourceType]]
  def readById(id: IDType)(connection: Connection): Option[ResourceType]
  def create(resourceType: ResourceType)(connection: Connection): Option[ResourceType]
  def update(id: IDType, resourceType: ResourceType)(connection: Connection): Option[ResourceType]
  def delete(id: IDType)(connection: Connection): Option[ResourceType]
}
