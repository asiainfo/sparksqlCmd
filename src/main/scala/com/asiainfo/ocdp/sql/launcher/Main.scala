package com.asiainfo.ocdp.sql.launcher

import java.io.File

import com.asiainfo.ocdp.sql.core.{Conf, Logging, SQLExecutionFactory}
import org.apache.commons.lang.StringUtils

/**
  * Created by peng on 2016/11/16.
  */
object Main extends Logging{
  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      System.err.println("Usage: export.sh <sql definition file path>")
      System.exit(1)
    }

    val sqlDefinitionPath = StringUtils.trimToEmpty(args(0))

    val sqlDefinitionFile = new File(sqlDefinitionPath)

    if (sqlDefinitionFile.exists && sqlDefinitionFile.isFile) {
      Conf.sqlDefinitionFilePath = sqlDefinitionPath

      SQLExecutionFactory.getSQLExecution.run
    }
    else{
      System.err.println("Can not find file '" + sqlDefinitionPath + "'.")
      System.exit(2)
    }

  }

}
