package com.howtographql.scala.sangria.models

import java.time.LocalDateTime

import sangria.validation.Violation

object models {
  case class Link(id: Int, url: String, description: String, createdAt: LocalDateTime)

//  object Link {
//    implicit val hasId = HasId[Link, Int](_.id)
//  }
  case class User(id: Int, name: String, email: String, password: String, createdAt: LocalDateTime)
  case class Vote(id: Int, createdAt: LocalDateTime, userId: Int, linkId: Int)

  case object LocalDateTimeCoerceViolation extends Violation {
    override def errorMessage: String = "Error during parsing LocalDateTime"
  }
}
