package app.repository

import java.sql.{Connection, DriverManager, ResultSet}

object MariaJDBC{
  val driver = "com.mysql.cj.jdbc.Driver"
  val url = "jdbc:mysql://db:3306"
  val username = "root"
  val password = "root" // better to have this encrypted and retrieved via a service,
  // never check-in an actual password.

  Class.forName(driver)

  implicit class ImprovedResult(result: ResultSet) {
    def map[T](f: (ResultSet) ⇒ T):List[T] = {
      if(result.next()){
        f(result) :: result.map(f)
      }else{
        List()
      }
    }
  }

  // create the statement, and run the select query
//  val statement = connection.createStatement()
//  val resultSet = statement.executeQuery(s"""SELECT host, user FROM user""")
//  while ( resultSet.next() ) {
//    val host = resultSet.getString("host")
//    val user = resultSet.getString("user")
//    println("host, user = " + host + ", " + user)
//  }

  def getConnection(): Connection = {
    DriverManager.getConnection(url, username, password) //hikari connection pool
  }

}
