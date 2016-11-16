package com.asiainfo.ocdp.sql.launcher

import com.asiainfo.ocdp.sql.core.{Logging, SQLExecutionFactory}

/**
  * Created by peng on 2016/11/16.
  */
object Main extends Logging{
  def main(args: Array[String]): Unit = {

    SQLExecutionFactory.getSQLExecution.run

  }

}
