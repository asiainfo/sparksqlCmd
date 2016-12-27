package com.asiainfo.ocdp.sql.impl

import java.util.concurrent.{CountDownLatch, Executors}

import com.asiainfo.ocdp.sql.core.{Conf, Logging, SQLExecution}
import org.apache.phoenix.jdbc.PhoenixDriver


/**
  * Created by peng on 2016/12/12.
  */
class PhoenixServerImpl extends SQLExecution with Logging{
  override def run: Unit = {
    logDebug("Start to run...")


    val queryServices = PhoenixDriver.INSTANCE.getQueryServices
//
//    val phoenixDriverExecutor = queryServices.getExecutor
//    phoenixDriverExecutor.setMaximumPoolSize(10000)
//    phoenixDriverExecutor.setCorePoolSize(10000)

    val executor = Executors.newCachedThreadPool();

    try
    {
      val latch = new CountDownLatch(Conf.allSQLDefinitions.size);
      logInfo("Begin executing...")
      val startTime = System.currentTimeMillis()

      Conf.allSQLDefinitions.foreach(sqlDefinition => {
        executor.execute(new SQLTask(latch, sqlDefinition))
      })

      latch.await()

      val endTime = System.currentTimeMillis()
      logInfo(s"All done and take ${endTime - startTime} ms.")

    } finally {
      if (null != executor){
        executor.shutdownNow()
      }
      PhoenixDriver.INSTANCE.close()
    }
  }
}
