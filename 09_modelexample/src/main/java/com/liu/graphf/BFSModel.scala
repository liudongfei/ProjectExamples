package com.liu.graphf

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{Column, Row, SaveMode, SparkSession}
import org.graphframes.GraphFrame

/**
  * @Auther: liudongfei
  * @Date: 2018/11/28 14:36
  * @Description:
  * BFS算法，在图中求出两个点之间的最短路径
  */

object BFSModel {
  def main(args: Array[String]): Unit = {
    val start = System.currentTimeMillis()
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    val conf = new SparkConf().setAppName("bfs").setMaster("local")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.memory.fraction", "0.4") //根据app的UI动态调整
      .set("spark.default.parallelism", "20")//最后设置为app的总core数量的2～3倍"
    val spark = SparkSession.builder().config(conf).getOrCreate()

    val danbaodf1 = spark.createDataFrame(List(
      ("1","一", "2", "二", "担保"),
      ("5","五", "6", "六", "担保"),
      ("7","七", "8", "八", "担保"),
      ("9","九", "10","十", "担保")
    )).toDF("cust_no","cust_name", "guar_no","guar_name", "rs")

    val duiwaidf2 = spark.createDataFrame(List(
      ("2","二", "3", "三", "对外"),
      ("5","五", "1", "一", "对外"),
      ("8","八", "9", "九", "对外"),
      ("10","十", "11","十一", "对外"),
      ("13","十三", "14", "十四", "对外"),
      ("15","十五", "16", "十六", "对外")
    )).toDF("cust_no","cust_name", "guar_no","guar_name", "rs")

    val gaoguandf3 = spark.createDataFrame(List(
      ("3","三", "4", "四", "高管"),
      ("11","十一", "7", "七", "高管"),
      ("14","十四", "15", "十五", "高管"),
      (null, null, "15", "十五", "高管")
    )).toDF("cust_no","cust_name", "guar_no","guar_name", "rs")

    val gudongdf4 = spark.createDataFrame(List(
      ("4", "四", "5", "五", "股东"),
      ("9", "九", "11", "十一", "股东"),
      ("12", "十二", "13", "十三", "股东"),
      ("16", "十六", "12", "十二", "股东"),
      (null, null, "17", "十七", "股东")
    )).toDF("cust_no","cust_name", "guar_no","guar_name", "rs")

    val df = duiwaidf2.union(gaoguandf3).union(gudongdf4).na.drop(Array("cust_no", "guar_no"))
    val danbao = danbaodf1.na.drop(Array("cust_no", "guar_no")).collect()

    val vertex = df.select("cust_no", "cust_name")
      .union(df.select("guar_no", "guar_name")).distinct()
      .withColumnRenamed("cust_no", "id")

    val edges = df.select("cust_no", "guar_no", "rs")
      .union(df.select("guar_no", "cust_no", "rs"))
      .withColumnRenamed("cust_no", "src")
      .withColumnRenamed("guar_no", "dst")
    val graph = GraphFrame(vertex, edges).cache()

    for (row <- danbao) {
      println(row.getString(0) + ":\t" + row.getString(2))
      graph.bfs.fromExpr("id='" + row.getString(0) + "'")
        .toExpr("id='" + row.getString(2) + "'")
        .edgeFilter("rs!='担保'")
        .maxPathLength(10)
        .run()
        .show()
    }
    val end = System.currentTimeMillis()
    println("共耗时:" + (end - start) / 1000 + "s")
  }
}
