package com.howtographql.scala.sangria.models

import java.time.LocalDateTime
import sangria.execution.deferred.HasId
import sangria.validation.Violation

object models {
  trait Identifiable {
    val id: Int
  }
  object Identifiable {
    implicit def hasId[T <: Identifiable]: HasId[T, Int] = HasId(_.id)
  }

  case class Link(id: Int, url: String, description: String, postedBy: Int, createdAt: LocalDateTime = LocalDateTime.now) extends Identifiable
  case class User(id: Int, name: String, email: String, password: String, createdAt: LocalDateTime = LocalDateTime.now) extends Identifiable
  case class Vote(id: Int,  userId: Int, linkId: Int, createdAt: LocalDateTime = LocalDateTime.now) extends Identifiable

  case object LocalDateTimeCoerceViolation extends Violation {
    override def errorMessage: String = "Error during parsing LocalDateTime"
  }
}
