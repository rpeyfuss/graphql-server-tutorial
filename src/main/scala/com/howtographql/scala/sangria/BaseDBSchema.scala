package com.howtographql.scala.sangria

import java.time.LocalDateTime
import slick.jdbc.H2Profile.api._
import com.howtographql.scala.sangria.utilities.DateTimeUtilities

trait BaseDBSchema {
  implicit val dateTimeColumnType = MappedColumnType.base[LocalDateTime, Long](
    dt => DateTimeUtilities.dateToInt(dt),
    ts => DateTimeUtilities.intToDate(ts),
  )
}
