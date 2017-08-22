package com.asiainfo.ocdp.sql.util

import java.io.{File, FileInputStream, IOException, InputStreamReader}
import java.util.Properties
import java.util.regex.Pattern

import com.asiainfo.ocdp.sql.core.{Conf, Logging}
import org.apache.commons.lang.StringUtils
import org.apache.spark.SparkException

import scala.collection.JavaConversions._
import scala.collection.Map
import scala.collection.mutable.ArrayBuffer

/**
  * Created by peng on 2016/11/16.
  */
object Utils extends Logging{



  /** Load properties present in the given file. */
  def getPropertiesFromFile(filename: String): Map[String, String] = {
    val file = new File(filename)
    require(file.exists(), s"Properties file $file does not exist")
    require(file.isFile(), s"Properties file $file is not a normal file")

    val inReader = new InputStreamReader(new FileInputStream(file), "UTF-8")
    try {
      val properties = new Properties()
      properties.load(inReader)
      properties.stringPropertyNames().map(k => (k, properties.getProperty(k).trim)).toMap
    } catch {
      case e: IOException =>
        throw new SparkException(s"Failed when loading Spark properties from $filename", e)
    } finally {
      inReader.close()
    }
  }

  def removeLastSeparator(value: String, separator: String): String = StringUtils.removeEnd(value, separator)

  def removeLastSeparator(value: StringBuilder, separator: String): String = removeLastSeparator(value.toString, separator)

  def sum(outputResult: ArrayBuffer[String], index: String): ArrayBuffer[String] ={

    val result = new ArrayBuffer[String]()
    if (outputResult.length > 0){
      if(Conf.properties.getOrElse(Conf.OUTPUT_TABLE_HEADER_ENABLE, "false").toBoolean){
        result.add(outputResult(0))
        outputResult.remove(0)
      }

      var sumValue = 0L
      outputResult.foreach(value =>{
        logDebug(s"sum from ${value}")
        sumValue = sumValue + value.split(Conf.properties.getOrElse(Conf.SEPARATOR, ","), -1)(index.toInt).toLong
      })

      logDebug(s"The sum is ${sumValue}")

      val row = new StringBuilder
      val columnSeparator = Conf.properties.getOrElse(Conf.SEPARATOR, ",")

      outputResult(0).split(columnSeparator, -1).toList.updated(index.toInt, sumValue.toString).foreach(v=>{
        row.append(v + columnSeparator)
      })

      result.add(Utils.removeLastSeparator(row.toString(), columnSeparator))
    }

    result
  }

  def getNumber(value: String): String ={
    val pattern = Pattern.compile("[^0-9]")
    val matcher = pattern.matcher(value)
    matcher.replaceAll("")
  }

}
