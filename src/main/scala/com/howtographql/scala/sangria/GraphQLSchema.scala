package com.howtographql.scala.sangria

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.howtographql.scala.sangria.models.models._
import sangria.ast.StringValue
import sangria.execution.deferred._
import sangria.macros.derive.{Interfaces, _}
import sangria.schema._

object GraphQLSchema {

  //this will parse LocalDateTime to String and vice-versa
  implicit val GraphQLDateTime = ScalarType[LocalDateTime](
    "DateTime",//2
    coerceOutput = (dt, _) => dt.toString, //3
    coerceInput = { //4
      case StringValue(dt, _, _ ) => Right(LocalDateTime.parse(dt))
      case _ => Left(LocalDateTimeCoerceViolation)
    },
    coerceUserInput = { //5
      case s: String => Right(LocalDateTime.parse(s.format(DateTimeFormatter.ofPattern("yyyy-mm-ddThh:mm:ss"))))
      case _ => Left(LocalDateTimeCoerceViolation)
    }
  )

  val IdentifiableType = InterfaceType(
    "Identifiable",
    fields[Unit, Identifiable](
      Field("id", IntType, resolve = _.value.id)
    )
  )
  //this will automatically map the sangria ObjectType to case class
  //replacing LocalDateTime to GraphQLDateTime
  implicit val LinkType = deriveObjectType[Unit, Link](
    Interfaces(IdentifiableType),
    ReplaceField("createdAt", Field("createdAt", GraphQLDateTime, resolve = _.value.createdAt))
  )
  implicit val UserType = deriveObjectType[Unit, User](
    Interfaces(IdentifiableType),
    ReplaceField("createdAt", Field("createdAt", GraphQLDateTime, resolve = _.value.createdAt))
  )
  implicit val VoteType = deriveObjectType[Unit, Vote](
    Interfaces(IdentifiableType),
    ReplaceField("createdAt", Field("createdAt", GraphQLDateTime, resolve = _.value.createdAt))
  )

  val Id = Argument("id", IntType)
  val Ids = Argument("ids", ListInputType(IntType))

  val linksFetcher = Fetcher ((ctx: MyContext, ids: Seq[Int]) => ctx.dao.getLinks(ids)
  )
  val usersFetcher = Fetcher ((ctx: MyContext, ids: Seq[Int]) => ctx.dao.getUsers(ids)
  )
  val votesFetcher = Fetcher ((ctx: MyContext, ids: Seq[Int]) => ctx.dao.getVotes(ids)
  )

  val Resolver = DeferredResolver.fetchers(linksFetcher, usersFetcher, votesFetcher)

  val QueryType = ObjectType(
    "Query",
    fields[MyContext, Unit](
      Field("allLinks", ListType(LinkType), resolve = c => c.ctx.dao.allLinks
      ),
      Field("link", OptionType(LinkType),
        arguments = Id :: Nil,
        resolve = c => linksFetcher.deferOpt(c.arg(Id))
      ),
      Field("links", ListType(LinkType),
        arguments = Ids :: Nil,
        resolve = c => linksFetcher.deferSeq(c.arg(Ids))
      ),
      Field("allUsers", ListType(UserType), resolve = c => c.ctx.dao.allUsers
      ),
      Field("user", OptionType(UserType),
        arguments = Id :: Nil,
        resolve = c => usersFetcher.deferOpt(c.arg(Id))
      ),
      Field("users", ListType(UserType),
        arguments = Ids :: Nil,
        resolve = c => usersFetcher.deferSeq(c.arg(Ids))
      )
    )
  )

  val SchemaDefinition = Schema(QueryType)
}
