package com.asiainfo.ocdp.sql.output

import com.asiainfo.ocdp.sql.core.Conf.SQLDefinition
import com.asiainfo.ocdp.sql.core.{Conf, Logging}
import org.apache.commons.lang.StringUtils

/**
  * Created by peng on 2016/12/13.
  */
class AssemblyImpl extends Assembly with Logging{
  override def execute(date: String, time: String, col: String, sqlDefinition: SQLDefinition): String = {
    val columnSeparator = StringUtils.defaultIfEmpty(sqlDefinition.outputSeparator, Conf.properties.getOrElse(Conf.SEPARATOR, ","))
    val result = new StringBuilder

    if(StringUtils.isNotEmpty(date)){
      result.append(date)
        .append(columnSeparator)
    }

    if(StringUtils.isNotEmpty(time)){
      result.append(time)
        .append(columnSeparator)
    }

    result.append(col)

    logDebug(s"Result is <${result}>")
    result.toString
  }
}
