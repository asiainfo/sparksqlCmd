package com.asiainfo.ocdp.sql.output

import com.asiainfo.ocdp.sql.core.{Conf, Logging}

/**
  * Created by peng on 2016/12/13.
  */
class PeriodFieldAssemblyimpl extends Assembly with Logging {
  override def execute(date: String, time: String, col: String): String = {
    val columnSeparator = Conf.properties.getOrElse(Conf.SEPARATOR, ",")
    val date_time: Array[String] = time.split(" ")
    val period_min: Array[String] = date_time(1).split(":")
    val result: StringBuilder = new StringBuilder
    result.append(date)
      .append(columnSeparator)
      .append(period_min(0).trim.toInt)
      .append(columnSeparator)
      .append(period_min(1).trim.toInt)
      .append(columnSeparator)
      .append(time)
      .append(columnSeparator)
      .append(col)

    logDebug(s"Result is <${result}>")
    result.toString
  }
}
