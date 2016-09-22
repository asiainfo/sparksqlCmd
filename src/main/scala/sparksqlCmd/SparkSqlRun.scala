package sparksqlCmd

import java.io.File
import java.sql.Timestamp
import java.util.Date
import java.text.SimpleDateFormat
import java.util.{Calendar, Properties, ArrayList}
import org.slf4j.LoggerFactory

import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkContext, SparkConf}

/**
  * Created by gengwang on 16/9/22.
  */
object SparkSqlRun {

  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {

    val formatter1: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val formatter2: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val formatter3: SimpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")

    //Get configuration from Conf

    val masterUrl = Conf.getProp("sparkMaster")
    val appName = Conf.getProp("AppName")
    val sparkHome = Conf.getProp("sparkHome")

    //get spark sql commands from config file
    val sparksql = Conf.getProp("sparksql").trim
    val sqlList = ( sparksql + ";" + "DummySplitHolder").split(";").dropRight(1).toSeq

    //get result path for saving query result
    val resultPath = Conf.getProp("resultPath")
    // file path format: /resultPath/appName/currentTime/sql sequence number
    val filePath = resultPath + File.separator + appName + File.separator + formatter3.format(new Timestamp(System.currentTimeMillis))

    //get outPut repartition
    val outputRepartition = Conf.getInt("outputRepartition", 1)

    val repartition = Conf.getInt("shuffleRepartition", 30)

    // Set spark sql context
    val conf = new SparkConf().setMaster(masterUrl)
      .setAppName(appName)
      .setSparkHome(sparkHome)

    val sc = new SparkContext(conf)
    val sqlContext = new HiveContext(sc)

    for(i<-1 to 6){
      try{

        sqlContext.udf.register("nowmonth", () => formatter1.format(new Timestamp(System.currentTimeMillis)))
        sqlContext.udf.register("nowseconds", () => formatter2.format(new Timestamp(System.currentTimeMillis)))

        logger.info("outputRepartition:"+outputRepartition)

        sqlContext.sql("SET spark.sql.shuffle.partitions=" + repartition.toString)

        var index = 0
        sqlList.foreach(sql => {
          logger.info("==================spark sql command:" + sql)
          if (!sql.isEmpty) {
            val df = sqlContext.sql(sql).repartition(outputRepartition)
            val fullPath = filePath +  File.separator + index.toString
            df.write.format("com.databricks.spark.csv").option("header", "false").save(fullPath)
            index = index + 1
          }
        })

        return
      }catch {
        case ex:Exception =>{
          if(i >= 5){
            throw ex
          }else{
            logger.warn("第"+i+"次执行spark sql失败！")
            logger.error(ex.getMessage)
            if(i>2){
              Thread.sleep(30000)
            }else{
              Thread.sleep(5000)
            }
          }
        }

      }
    }

  }

}