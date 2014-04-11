package com.sequenceiq.samples.scalding

import org.joda.time.DateTime
import java.text.SimpleDateFormat

object DataConverter {
  def convertDataToInt(data: String): Int = data.toInt

  def convertDataToIntList(data: String): List[Int] = data.split(",").toList map (_.toInt)

  def convertDataToStringList(data: String): List[String] = data.split(",").toList

  def convertDataToDouble(data: String): Double = data.toDouble

  def convertDataToDoubleList(data: String): List[Double] = data.split(",").toList map (_.toDouble)

  def convertDataToDateTime(data: String, pattern: String): DateTime = new DateTime(new SimpleDateFormat(pattern).parse(data))

  def convertDataToDateTimeList(data: String, pattern: String): List[DateTime] = {
    for (d <- data.split(",").toList) yield new DateTime(new SimpleDateFormat(pattern).parse(d))
  }

}
