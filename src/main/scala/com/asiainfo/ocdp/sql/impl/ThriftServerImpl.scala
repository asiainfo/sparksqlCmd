package com.asiainfo.ocdp.sql.impl

import java.io.File
import java.sql.{Connection, DriverManager, PreparedStatement}

import com.asiainfo.ocdp.sql.core.{Conf, Logging, SQLExecution}
import org.apache.commons.io.FileUtils

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._

/**
  * Created by peng on 2016/11/16.
  */
class ThriftServerImpl extends SQLExecution with Logging{
  override def run: Unit = {
    Class.forName("org.apache.hive.jdbc.HiveDriver").newInstance()

    var connection: Connection = null
    var statement: PreparedStatement = null

    try
    {
      connection = DriverManager.getConnection(Conf.properties("jdbc.uri"))


      Conf.allSQLDefinitions.foreach(sqlDefinition => {

        val sql = sqlDefinition.sql

        logInfo(s"---- Begin executing sql: $sql ----")
        statement = connection.prepareStatement(sql)

        val result = statement.executeQuery()
        val resultMetaData = result.getMetaData
        val colNum = resultMetaData.getColumnCount

        val aa = new ArrayBuffer[String]


        while (result.next()) {
          val sb = new StringBuilder
          for (i <- 1 to colNum) {
            sb.append(result.getString(i) + "\t")
          }

          aa += sb.toString()
        }

        val startTime = System.currentTimeMillis()
        logInfo("Start to export")
        FileUtils.writeLines(new File(sqlDefinition.outputPath), aa)
        val endTime = System.currentTimeMillis()
        logInfo("End for " + (endTime - startTime))


        logInfo(s"---- Done executing sql: $sql to " + sqlDefinition.outputPath +"----")

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
