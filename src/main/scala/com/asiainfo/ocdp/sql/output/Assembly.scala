package com.asiainfo.ocdp.sql.output

import com.asiainfo.ocdp.sql.core.Conf.SQLDefinition

/**
  * Created by peng on 2016/12/13.
  */
trait Assembly {
  def execute(date: String, time: String, col: String, sqlDefinition: SQLDefinition): String
}
