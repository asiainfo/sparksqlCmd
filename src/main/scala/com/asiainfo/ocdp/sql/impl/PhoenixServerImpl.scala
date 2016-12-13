package com.asiainfo.ocdp.sql.impl

import java.sql.{Connection, DriverManager, PreparedStatement}
import java.util.concurrent.{CountDownLatch, Executors}

import com.asiainfo.ocdp.sql.core.{Conf, Logging, SQLExecution}

/**
  * Created by peng on 2016/12/12.
  */
class PhoenixServerImpl extends SQLExecution with Logging{
  override def run: Unit = {
    logDebug("Start to run...")

    Class.forName("org.apache.phoenix.jdbc.PhoenixDriver").newInstance()

    var connection: Connection = null
    var statement: PreparedStatement = null
    val executor = Executors.newCachedThreadPool();

    try
    {
      connection = DriverManager.getConnection(Conf.properties("jdbc.uri"))

      val latch = new CountDownLatch(Conf.allSQLDefinitions.size);
      logInfo("Begin executing...")
      val startTime = System.currentTimeMillis()

      Conf.allSQLDefinitions.foreach(sqlDefinition => {

        val sql = sqlDefinition.sql
        statement = connection.prepareStatement(sql)

        executor.execute(new SQLTask(latch, connection, statement, sqlDefinition))

      })

      latch.await()

      val endTime = System.currentTimeMillis()
      logInfo(s"All done and take ${endTime - startTime} ms.")

    } finally {
      if (null != statement) {
        statement.close()
      }
      if (null != connection) {
        connection.close()
      }
      if (null != executor){
        executor.shutdownNow()
      }
    }
  }
}
