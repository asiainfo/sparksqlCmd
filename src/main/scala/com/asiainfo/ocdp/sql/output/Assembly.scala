package com.asiainfo.ocdp.sql.output

/**
  * Created by peng on 2016/12/13.
  */
trait Assembly {
  def execute(date: String, time: String, col: String): String
}
