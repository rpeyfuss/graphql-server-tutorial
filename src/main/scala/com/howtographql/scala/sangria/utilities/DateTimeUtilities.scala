package com.howtographql.scala.sangria.utilities

import java.time.LocalDateTime

object DateTimeUtilities {
  type YYYYMMDDHHMMSS = Long

  def intToDate(date: YYYYMMDDHHMMSS): LocalDateTime = {
    val dateStr = date.toString
    val year = dateStr.substring(0, 4).toInt
    val month = dateStr.substring(4, 6).toInt
    val day = dateStr.substring(6, 8).toInt
    val hour = dateStr.substring(8, 10).toInt
    val minute = dateStr.substring(10, 12).toInt
    val second = dateStr.substring(12, 14).toInt
    LocalDateTime.of(year, month, day, hour, minute, second)
  }

  def dateToInt(date: LocalDateTime): YYYYMMDDHHMMSS = {
    val str =  "" + date.getYear +
      pad2Zero(date.getMonthValue) +
      pad2Zero(date.getDayOfMonth) +
      pad2Zero(date.getHour) +
      pad2Zero(date.getMinute) +
      pad2Zero(date.getSecond)
      str.toLong
  }

  def pad2Zero(num: Int): String = {
    if (num.toString.length < 2)
      "0"+num.toString
    else
      num.toString
  }
}
