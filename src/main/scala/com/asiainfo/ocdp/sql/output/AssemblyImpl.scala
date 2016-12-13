package com.asiainfo.ocdp.sql.output

import com.asiainfo.ocdp.sql.core.{Conf, Logging}

/**
  * Created by peng on 2016/12/13.
  */
class AssemblyImpl extends Assembly with Logging{
  override def execute(date: String, time: String, col: String): String = {
    val columnSeparator = Conf.properties.getOrElse(Conf.SEPARATOR, ",")
    val result = new StringBuilder
    result.append(date)
      .append(columnSeparator)
      .append(time)
      .append(columnSeparator)
      .append(col)

    logDebug(s"Result is <${result}>")
    result.toString
  }
}
