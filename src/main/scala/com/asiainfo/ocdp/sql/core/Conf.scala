package com.asiainfo.ocdp.sql.core

import java.io.File
import java.nio.file.Paths

import com.asiainfo.ocdp.sql.util.Utils

import scala.collection.mutable
import com.owlike.genson.defaultGenson._
import org.apache.commons.io.FileUtils


/**
  * Created by peng on 2016/11/15.
  */
object Conf extends Logging{

  case class SQLDefinition(name:String, sql: String, outputPath: String, alias: String, dateFormat: String, timeFormat: String, handlerClass: String, groupId: String)

  val HOME_PATH = Paths.get(classOf[SQLDefinition].getProtectionDomain.getCodeSource.getLocation.getPath).getParent.getParent.toString
  val log4jFilePath = HOME_PATH + File.separator + "conf" + File.separator + "log4j.properties"
  val confFilePath = HOME_PATH + File.separator + "conf" + File.separator + "running.properties"
  val sparkConfFilePath = HOME_PATH + File.separator + "conf" + File.separator + "spark-yarn.properties"
  val DEFAULT_HANDLER_CLASS = "com.asiainfo.ocdp.sql.output.AssemblyImpl"

  val BASE_OUTPUT_DIR = HOME_PATH + File.separator + "result"
  val TABLE_FILE_TYPE = ".txt"
  val EXPORT_INTERVAL_S = "export.interval-s"
  val DEFAULT_EXPORT_INTERVAL_S = 600L

  val REUSED_CONNECTION_ENABLE = "reused.connection.enable"
  val DEFAULT_REUSED_CONNECTION_ENABLE = false

  var sqlDefinitionFilePath = ""

  val OUTPUT_TABLE_HEADER_ENABLE = "output.table.header.enable"
  val SEPARATOR = "output.table.column.separator"

  val DEFAULT_GROUP_ID = "default_group"
  val PHOENIX_QUERY_THREADPOOLSIZE = "phoenix.query.threadPoolSize"

  val properties: mutable.HashMap[String, String] = {
    val defaultProperties = new mutable.HashMap[String, String]()

      Utils.getPropertiesFromFile(confFilePath).foreach { case (k, v) =>
        defaultProperties(k) = v
      }

    defaultProperties
  }

  lazy val allSQLDefinitions = fromJson[List[SQLDefinition]](FileUtils.readFileToString(new File(sqlDefinitionFilePath), "UTF-8"))

}
