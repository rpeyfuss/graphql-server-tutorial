package com.howtographql.scala.sangria

import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, OK}
import akka.http.scaladsl.server.Directives._
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.parser.QueryParser
import akka.http.scaladsl.server.Route
import spray.json.{JsObject, JsString, JsValue}
import sangria.ast.Document
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import sangria.marshalling.sprayJson._
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object GraphQLServer {
  private val dao = DBSchema.createDatabase

  def endpoint(requestJSON: JsValue)(implicit ec: ExecutionContext): Route = {
    val JsObject(fields) = requestJSON
    val JsString(query) = fields("query")

    QueryParser.parse(query) match {
      case Success(queryAst) =>
        val operation = fields.get("operationName") collect {
          case JsString(op) => op
        }
        val variables = fields.get("variables") match {
          case Some(obj: JsObject) => obj
          case _ => JsObject.empty
        }
        complete(executeGraphQLQuery(queryAst, operation, variables))
      case Failure(error) =>
        complete(BadRequest, JsObject("error" -> JsString(error.getMessage)))
    }
  }

  private def executeGraphQLQuery(query: Document, operation: Option[String], vars: JsObject)(implicit ec: ExecutionContext) = {
    Executor.execute(
      GraphQLSchema.SchemaDefinition,
      query,
      MyContext(dao),
      variables = vars,
      operationName = operation,
      deferredResolver = GraphQLSchema.Resolver
    ).map(OK -> _)
      .recover {
        case error: QueryAnalysisError => BadRequest -> error.resolveError
        case error: ErrorWithResolver => InternalServerError -> error.resolveError
      }
  }

}
