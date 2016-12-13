package com.asiainfo.ocdp.sql.impl

import java.io.File
import java.sql.{Connection, PreparedStatement}
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.CountDownLatch

import com.asiainfo.ocdp.sql.core.Conf.SQLDefinition
import com.asiainfo.ocdp.sql.core.{Conf, Logging}
import com.asiainfo.ocdp.sql.output.Assembly
import com.asiainfo.ocdp.sql.util.Utils
import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.math.NumberUtils

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._

/**
  * Created by peng on 2016/12/13.
  */
class SQLTask(downLatch: CountDownLatch, connection: Connection, statement: PreparedStatement, sqlDefinition: SQLDefinition) extends Runnable with Logging {
  override def run(): Unit = {
    try{
      val sql = sqlDefinition.sql
      val columnSeparator = Conf.properties.getOrElse(Conf.SEPARATOR, ",")

      logInfo(s"Begin executing sql: $sql")
      val startTime = System.currentTimeMillis()

      val sqlResult = statement.executeQuery()
      logInfo("Executed query command successfully.")

      val resultMetaData = sqlResult.getMetaData
      val colNum = resultMetaData.getColumnCount

      val outputResult = new ArrayBuffer[String]

      if (Conf.properties.getOrElse(Conf.OUTPUT_TABLE_HEADER_ENABLE, "false").toBoolean) {
        val row = new StringBuilder
        for (i <- 1 to colNum) {
          row.append(resultMetaData.getColumnName(i) + columnSeparator)
        }
        outputResult += Utils.removeLastSeparator(row.toString(), columnSeparator)
      }
      val dateFormat = new SimpleDateFormat(sqlDefinition.dateFormat)
      val timeFormat = new SimpleDateFormat(sqlDefinition.timeFormat)
      val date = new Date

      val newoneClass: Class[_] = Class.forName(StringUtils.defaultString(sqlDefinition.handlerClass, Conf.DEFAULT_HANDLER_CLASS))
      val assembly = newoneClass.newInstance.asInstanceOf[Assembly]

      while (sqlResult.next()) {
        val row = new StringBuilder
        for (i <- 1 to colNum) {
          row.append(sqlResult.getString(i) + columnSeparator)
        }

        outputResult += assembly.execute(dateFormat.format(date), timeFormat.format(date), Utils.removeLastSeparator(row.toString(), columnSeparator))
      }


      logInfo(s"Start to export to '${sqlDefinition.name}'...")

      val outputPath = outputResultData(outputResult, sqlDefinition)

      val endTime = System.currentTimeMillis()

      logInfo(s"Done executing sql: $sql to $outputPath and take ${endTime - startTime} ms.")
    }
    catch {
      case ex: Exception => {
        logError("Unexpected Error:", ex)
      }
    }
    finally {
      downLatch.countDown()
    }

  }

  def outputResultData(outputResult: ArrayBuffer[String], sqlDefinition: SQLDefinition): String ={
    val date = new Date
    val postfixFormat = new SimpleDateFormat("yyyy-MM-dd")
    val fileDateFormat = new SimpleDateFormat("yyyyMMdd")
    val startTimeStr = postfixFormat.format(new Date) + " 00:00"
    val startTimeLong = postfixFormat.parse(startTimeStr).getTime
    val currentFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    val currentTimeStr = currentFormat.format(date)
    val currentTimeLong = currentFormat.parse(currentTimeStr).getTime
    val filePostfix = String.valueOf((currentTimeLong - startTimeLong) / 1000 / NumberUtils.toLong(Conf.EXPORT_INTERVAL_S, Conf.DEFAULT_EXPORT_INTERVAL_S))

    val outputDir = StringUtils.defaultString(sqlDefinition.outputPath, Conf.BASE_OUTPUT_DIR)
    val filePath = outputDir + File.separator + sqlDefinition.name + "-" + fileDateFormat.format(date) + "_" + filePostfix + Conf.TABLE_FILE_TYPE

    FileUtils.writeLines(new File(filePath), outputResult)

    filePath
  }
}
