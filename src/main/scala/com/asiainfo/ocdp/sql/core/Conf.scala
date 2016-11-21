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

  case class SQLDefinition(sql: String, outputPath: String, alias: String)

  //val properties = new Properties()

  val HOME_PATH = Paths.get(classOf[SQLDefinition].getProtectionDomain.getCodeSource.getLocation.getPath).getParent.getParent.toString
  val log4jFilePath = HOME_PATH + File.separator + "conf" + File.separator + "log4j.properties";
  val confFilePath = HOME_PATH + File.separator + "conf" + File.separator + "running.properties";

  var sqlDefinitionFilePath = "";

  val OUTPUT_TABLE_HEADER_ENABLE = "output.table.header.enable"
  val SEPARATOR = "output.table.column.separator"

  val properties: mutable.HashMap[String, String] = {
    val defaultProperties = new mutable.HashMap[String, String]()

      Utils.getPropertiesFromFile(confFilePath).foreach { case (k, v) =>
        defaultProperties(k) = v
      }

    defaultProperties
  }

  lazy val allSQLDefinitions = fromJson[List[SQLDefinition]](FileUtils.readFileToString(new File(sqlDefinitionFilePath), "UTF-8"))

}
