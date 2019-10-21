package com.howtographql.scala.sangria

import java.time.LocalDateTime

import com.howtographql.scala.sangria.models.models.{Link, User, Vote}
import com.howtographql.scala.sangria.utilities.DateTimeUtilities
import slick.jdbc.H2Profile.api._

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps

object DBSchema {

  implicit val dateTimeColumnType = MappedColumnType.base[LocalDateTime, Long](
    dt => DateTimeUtilities.dateToInt(dt),
    ts => DateTimeUtilities.intToDate(ts),
  )
  class LinksTable(tag: Tag) extends Table[Link](tag, "LINKS") {

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def url = column[String]("URL")
    def description = column[String]("DESCRIPTION")
    def createdAt = column[LocalDateTime]("CREATED_AT")
    def * = (id, url, description, createdAt).mapTo[Link]
  }
  val Links = TableQuery[LinksTable]

  class UsersTable(tag: Tag) extends Table[User](tag, "USERS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def email = column[String]("EMAIL")
    def password = column[String]("PASSWORD")
    def createdAt = column[LocalDateTime]("CREATED_AT")
    def * = (id, name, email, password, createdAt).mapTo[User]
  }
  val Users = TableQuery[UsersTable]

  class VotesTable(tag: Tag) extends Table[Vote](tag, "VOTES") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def createdAt = column[LocalDateTime]("CREATED_AT")
    def userId = column[Int]("USER_ID")
    def linkId = column[Int]("LINK_ID")
    def * = (id, createdAt, userId, linkId).mapTo[Vote]
  }
  val Votes = TableQuery[VotesTable]

  /**
    * Load schema and populate sample data withing this Sequence od DBActions
    */
  val databaseSetup = DBIO.seq(
    Links.schema.create,
    Links forceInsertAll Seq(
      Link(1, "http://howtographql.com", "Awesome community driven GraphQL tutorial", LocalDateTime.of(2019, 1,1, 1, 1,1)),
      Link(2, "http://graphql.org", "Official GraphQL web page", LocalDateTime.of(2019, 1,1, 1, 1,1)),
      Link(3, "https://facebook.github.io/graphql/", "GraphQL specification", LocalDateTime.of(2019, 1,1, 1, 1,1))
    ),
    Users.schema.create,
    Users forceInsertAll Seq(
      User(1, "John Doe", "john.doe@example.com", "abcdef", LocalDateTime.of(2019, 1,1, 1, 1,1)),
      User(2, "Mary Jane", "mary.jane@example.com", "abcdef", LocalDateTime.of(2019, 1,1, 1, 1,1))
    ),
    Votes.schema.create,
    Votes forceInsertAll Seq(
     Vote(1,LocalDateTime.of(2019, 1,1, 1, 1,1), 1, 1),
     Vote(2,LocalDateTime.of(2019, 1,1, 1, 1,1), 1, 2)
    )
  )

  def createDatabase: DAO = {
    val db = Database.forConfig("h2mem")
    Await.result(db.run(databaseSetup), 10 seconds)
    new DAO(db)
  }


}
