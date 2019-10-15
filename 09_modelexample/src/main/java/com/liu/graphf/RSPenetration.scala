package com.liu.graphf

import java.text.SimpleDateFormat
import java.util.Date

import com.liu.udf.StringPlus
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}
import org.graphframes.GraphFrame
import org.slf4j.LoggerFactory

import scala.collection.mutable.ArrayBuffer

/**
  * @Auther: liudongfei
  * @Date: 2019/1/9 14:07
  * @Description:关联关系穿透模型
  */
object RSPenetration {
  def sumRS(cnt: Column, rs:Column) : Column = {
    when(rs === "担保", cnt + 1).otherwise(cnt)
  }
  def getNowDate(): String= {
    new SimpleDateFormat("yyyy-MM-dd").format(new Date())
  }
  def main(args: Array[String]): Unit = {
    val start = System.currentTimeMillis()
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    val logger = LoggerFactory.getLogger("rspenetration")
    val conf = new SparkConf().setAppName("bfs")
    val spark = SparkSession.builder().master("local").config(conf).getOrCreate()
    //担保关系表
    val danbaodf1 = spark.createDataFrame(List(
      ("1","一", "2", "二", "担保"),
      ("5","五", "6", "六", "担保"),
      ("7","七", "8", "八", "担保"),
      ("9","九", "10","十", "担保")
    )).toDF("cust_no","cust_name", "guar_no","guar_name", "rs")
    //对外关系表
    val duiwaidf2 = spark.createDataFrame(List(
      ("2","二", "3", "三", "对外"),
      ("5","五", "1", "一", "对外"),
      ("8","八", "9", "九", "对外"),
      ("10","十", "11","十一", "对外"),
      ("13","十三", "14", "十四", "对外"),
      ("15","十五", "16", "十六", "对外")
    )).toDF("cust_no","cust_name", "guar_no","guar_name", "rs")
    //高管关系表
    val gaoguandf3 = spark.createDataFrame(List(
      ("3","三", "4", "四", "高管"),
      ("11","十一", "7", "七", "高管"),
      ("14","十四", "15", "十五", "高管"),
      (null, null, "15", "十五", "高管")
    )).toDF("cust_no","cust_name", "guar_no","guar_name", "rs")
    //股东关系表
    val gudongdf4 = spark.createDataFrame(List(
      ("4", "四", "5", "五", "股东"),
      ("9", "九", "11", "十一", "股东"),
      ("12", "十二", "13", "十三", "股东"),
      ("16", "十六", "12", "十二", "股东"),
      (null, null, "17", "十七", "股东")
    )).toDF("cust_no","cust_name", "guar_no","guar_name", "rs")
    //数据预处理，删除值为na的记录，合并除了担保关系之外的数据表
    val df = duiwaidf2
      .union(gaoguandf3).union(gudongdf4)
      .na.drop(Array("cust_no", "guar_no")).repartition(200)
    //数据预处理，删除只为na的记录，
    val df2 = danbaodf1.na.drop(Array("cust_no", "guar_no"))
    //计算图的顶点，（cust_no,cust_name)
    val vertex = df.select("cust_no", "cust_name")
      .union(df.select("guar_no", "guar_name"))
      .distinct()
      .withColumnRenamed("cust_no", "id")
    //计算图的边，(src, dst, rs)
    spark.udf.register("stringPlus", new StringPlus)
    val edges = df.select("cust_no", "guar_no", "rs")
      .union(df.select("guar_no", "cust_no", "rs"))
      .union(df2.select("guar_no", "cust_no", "rs"))
      .withColumnRenamed("cust_no", "src")
      .withColumnRenamed("guar_no", "dst")
      .groupBy("src", "dst")
      .agg(expr("stringPlus(rs) as rs"))
    //      .withColumn("rs",col("rs").cast(StringType))
    edges
      .show()
    //构建关系图谱
    val graph = GraphFrame(vertex, edges).cache()
    //关系链的最大长度
    val len = 5
    //含有担保关系的关系链的记录表
    var circleDF: Dataset[Row] = null
    //从最小长度（3），到最大长度（len），开始查找关系链
    for(i <- 3 until len + 1){
      //关系链表达式
      var expr = ""
      //关系链上的edge的集合
      var condSeq1= ArrayBuffer[String]()
      //关系链的过滤条件
      var filterCond = ""
      //构建关系链表达式，edge集合，关系链过滤条件
      for(j <- 0 until i ){
        for(k <- 0 until i){
          if(j < k){
            filterCond += " and a" + j + ".id!=a" + k + ".id"
          }
        }
        expr += "(a" + j + ")-[e" + (j + 1) + "]->(a" + (j + 1) +");"
        condSeq1 +="e" + (j + 1)
      }
      expr = expr.replace("(a" + i + ");", "(a0)")
      filterCond = filterCond.replaceFirst(" and ", "")
      //使用关系链上的edge集合构造过滤函数
      val whereCond1 = condSeq1.foldLeft(lit(0))((cnt, e) => sumRS(cnt, col(e)("rs")))
      logger.info("关系链表达式：\t" + expr)
      logger.info("关系链上的edge的集合：\t" + condSeq1)
      logger.info("关系链的过滤条件：\t" + filterCond)
      //寻找担保关系链，（"cust_no,cust_name,guar_no,guai_name", edges, length)）
      val rdd1 = graph.find(expr)
        .where(whereCond1 === 1)//过滤出关系链上第一条边是"担保"关系的关系链
        .filter(col("e1.rs").equalTo("担保"))
        .filter(filterCond)//过滤出关系链上所有点都不同的关系链
        .rdd//[[1],[1,5,对外],[5],[5,4,股东],[4],[4,3,高管],[3],[3,2,对外],[2]]
        .map(row => {
        val circleLine = row.toString().replaceAll("\\[\\[","").replaceAll("\\]\\]", "")
        val circleLineEle = circleLine.split("\\],\\[")
        val circleStart = circleLineEle(2)//担保关系链的起点，cust_no,cust_name
        val circleEnd = circleLineEle(0)//担保关系的终点，guar_no,guar_name
        var tmp = ""//存储担保关系链中间的边，cust_no,cust_name,rs,guar_no,guar_name,cust_no,cust_name,rs,guar_no,guar_name
        for(m <- 2 until circleLineEle.length){
          if(circleLineEle(m).split(",").length == 2){
            tmp += circleLineEle(m) + ","
          } else if(circleLineEle(m).split(",").length == 3) {
            tmp += circleLineEle(m).split(",")(2) +","
          }
        }
        tmp += circleEnd
        Row(circleStart + "," + circleEnd, tmp, i)
      })
      //创建临时担保关系链表，列为（src_dst,edges,length)
      val structType1 = StructType(Array(StructField("src_dst", DataTypes.StringType, true),
        StructField("edges", DataTypes.StringType, true),
        StructField("length", DataTypes.IntegerType,true)))
      val tmpDF = spark.createDataFrame(rdd1, structType1)
      //将临时担保关系链表加入总担保关系链表
      if(i == 3){
        circleDF = tmpDF
      } else {
        circleDF = circleDF.union(tmpDF)
      }
      println(i)
    }
    //针对担保关系链的起点和终点，筛选出最短的担保关系链，如果有多条则都保存
    val tmpDF1 = circleDF.groupBy("src_dst").agg(("length", "min"))
      .withColumnRenamed("min(length)", "length")
      .join(circleDF, Seq("src_dst", "length"), "inner")
    //对担保关系链表的总表添加序号列
    val w = Window.orderBy("src_dst")
    val tmpDF2 = tmpDF1.withColumn("circleIndex", row_number().over(w))

    /**
      * 对担保关系链拆分成边的形式，
      * （cust_no,cust_name,guar_no,guar_name,reality_cust_no1,reality_cust_name1,
      * reality_cust_no2,reality_cust_name2,circleIndex,index,rs)
      */

    val rdd2 = tmpDF2.rdd.flatMap(row =>{
      val circleIndex = row.getAs[Int]("circleIndex")
      val tmp = row.getAs[String]("edges")
      val startNo = row.getAs[String]("src_dst").split(",")(0)
      val startName = row.getAs[String]("src_dst").split(",")(1)
      val endNo = row.getAs[String]("src_dst").split(",")(2)
      val endName = row.getAs[String]("src_dst").split(",")(3)
      var edgeIndex = 1
      val circleEdgeList = ArrayBuffer[Row]()
      //从存储担保关系链中间的边的tmp中拆分出每一条边
      for(n <- 0 until(tmp.split(",").length, 3)){
        var edge = ""
        for(q <- 0 until 5){
          if((n + q) < tmp.split(",").length){
            edge += "," +tmp.split(",")(n + q)
          } else {
            edge = ""
          }
        }
        if(!edge.equals("")){
          edge = edge.replaceFirst(",", "")
          val edgeEle = edge.split(",")
          circleEdgeList += Row(startNo,startName,endNo,endName,edgeEle(0),edgeEle(1),edgeEle(3),edgeEle(4),circleIndex,edgeIndex,edgeEle(2))
          edgeIndex += 1
        }
      }
      circleEdgeList
    })

    val structType2 = StructType(Array(StructField("cust_no", DataTypes.StringType, true),
      StructField("cust_name", DataTypes.StringType, true),
      StructField("guar_no", DataTypes.StringType, true),
      StructField("guar_name", DataTypes.StringType,true),
      StructField("reality_cust_no1",DataTypes.StringType, true),
      StructField("reality_cust_name1", DataTypes.StringType, true),
      StructField("reality_cust_no2", DataTypes.StringType, true),
      StructField("reality_cust_name2", DataTypes.StringType, true),
      StructField("circleIndex", DataTypes.IntegerType, true),
      StructField("index", DataTypes.IntegerType,true),
      StructField("rs", DataTypes.StringType, true)))
    //添加日期model_date列，然后存储成为csv格式
    spark.createDataFrame(rdd2, structType2).withColumn("model_date", current_date()).repartition(1)
      .write.mode(SaveMode.Overwrite).format("csv")
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .option("header", "true")
      .save("hdfs:///user/liudongfei/spark/output/modelresult")


    val end = System.currentTimeMillis()

    println("共耗时:" + (end - start) / 1000 + "s")
  }
}
