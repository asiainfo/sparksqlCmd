package com.asiainfo.ocdp.sql.impl

import com.asiainfo.ocdp.sql.core.{Conf, Logging, SQLExecution}
import org.apache.commons.lang.StringUtils
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by peng on 2016/11/16.
  */
class HiveContextImpl extends SQLExecution with Logging{
  override def run: Unit = {
    val sparkConf = new SparkConf().setAppName("HiveContextImpl")
    val sc = new SparkContext(sparkConf)

    val hiveContext = new HiveContext(sc)
    import hiveContext.sql

    Conf.allSQLDefinitions.foreach(sqlDefinition => {
      sql(StringUtils.trimToEmpty(sqlDefinition.sql)).write.format("com.databricks.spark.csv").option("header", Conf.properties.getOrElse(Conf.OUTPUT_TABLE_HEADER_ENABLE, "false")).save(sqlDefinition.outputPath)
    })

    sc.stop()
  }
}
