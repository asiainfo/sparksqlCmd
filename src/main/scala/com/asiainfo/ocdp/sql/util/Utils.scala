package com.asiainfo.ocdp.sql.util

import java.io.{File, FileInputStream, IOException, InputStreamReader}
import java.util.Properties

import org.apache.spark.SparkException

import scala.collection.JavaConversions._
import scala.collection.Map

/**
  * Created by peng on 2016/11/16.
  */
object Utils {



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
}
