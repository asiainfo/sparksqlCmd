package com.asiainfo.ocdp.sql.impl

import java.io.File
import java.sql.{Connection, DriverManager, PreparedStatement}

import com.asiainfo.ocdp.sql.core.{Conf, Logging, SQLExecution}
import com.asiainfo.ocdp.sql.util.Utils
import org.apache.commons.io.FileUtils

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._

/**
  * Created by peng on 2016/11/16.
  */
class ThriftServerImpl extends SQLExecution with Logging{
  override def run: Unit = {
    logDebug("Start to run...")

    Class.forName("org.apache.hive.jdbc.HiveDriver").newInstance()

    var connection: Connection = null
    var statement: PreparedStatement = null

    try
    {
      connection = DriverManager.getConnection(Conf.properties("jdbc.uri"))
      val columnSeparator = Conf.properties.getOrElse(Conf.SEPARATOR, ",")


      Conf.allSQLDefinitions.foreach(sqlDefinition => {

        val sql = sqlDefinition.sql

        logInfo(s"Begin executing sql: $sql")
        val startTime = System.currentTimeMillis()
        statement = connection.prepareStatement(sql)

        val sqlResult = statement.executeQuery()
        logInfo("Executed query command successfully.")

        val resultMetaData = sqlResult.getMetaData
        val colNum = resultMetaData.getColumnCount

        val outputResult = new ArrayBuffer[String]

        if (Conf.properties.getOrElse(Conf.OUTPUT_TABLE_HEADER_ENABLE, "false").toBoolean){
          val row = new StringBuilder
          for (i <- 1 to colNum) {
            row.append(resultMetaData.getColumnName(i) + columnSeparator)
          }
          outputResult += Utils.removeLastSeparator(row.toString(), columnSeparator)
        }

        while (sqlResult.next()) {
          val row = new StringBuilder
          for (i <- 1 to colNum) {
            row.append(sqlResult.getString(i) + columnSeparator)
          }

          outputResult += Utils.removeLastSeparator(row.toString(), columnSeparator)
        }

        val outputPath = sqlDefinition.outputPath

        logInfo(s"Start to export to '$outputPath'...")

        FileUtils.writeLines(new File(outputPath), outputResult)

        val endTime = System.currentTimeMillis()

        logInfo(s"Done executing sql: $sql to $outputPath and take ${endTime - startTime} ms.")

      })
    } finally {
      if (null != statement) {
        statement.close()
      }
      if (null != connection) {
        connection.close()
      }
    }
  }
}
