package com.liu.graphf

import com.liu.udf.StringPlus
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.{col, countDistinct, expr, udf}
import org.graphframes.GraphFrame
import org.slf4j.LoggerFactory

/**
  * @Auther: liudongfei
  * @Date: 2019/1/9 14:03
  * @Description:
  */
object GraphDemo2 {
  /**
    * 验证身份证号的正确性
    * @param certId
    * @return
    */
  private def checkCertId(certId : String): Boolean ={
    val arr=Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)
    val dicts = Map(
      0 -> '1',
      1 -> '0',
      2 -> 'X',
      3 -> '9',
      4 -> '8',
      5 -> '7',
      6 -> '6',
      7 -> '5',
      8 -> '4',
      9 -> '3',
      10 -> '2')
    if(certId.length()!= 18){
      false
    } else {
      try{
        var checkVal = 0
        for (i <- 0 until 17){
          println(Integer.valueOf(certId(i).toString))
          checkVal += arr(i) * (Integer.valueOf(certId(i).toString()))
        }
        println(checkVal)
        val checkValue = dicts.get(checkVal % 11)
        if(checkValue.getOrElse(" ") == certId(17)){
          true
        } else {
          false
        }
      } catch {
        case ex : Exception => false
      }
    }
  }
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    val conf = new SparkConf().setAppName("bfs")
    val spark = SparkSession.builder().master("local").config(conf).getOrCreate()
    //担保关系表
    val danbaodf1 = spark.createDataFrame(List(
      ("1","一", "2", "二", "担!保"),
      ("5","五", "6", "六", "担!保"),
      ("7","七", "8", "八", "担!保"),
      ("9","九", "10","十", "担!保")
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
      .na.drop(Array("cust_no", "guar_no"))
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
      .groupBy("src", "dst").agg(expr("stringPlus(rs) as rs"))


    val test = spark.createDataFrame(List(("", "")
    )).toDF("cust_no","cust_name")
    test.show()
    edges.show()
    //    构建关系图谱
    val graph = GraphFrame(vertex, edges)
    val result = graph.connectedComponents.setAlgorithm("graphx").run()
    result.orderBy("component").show()
    val code : ((String, String) => String) = (arg1 : String, arg2 : String) => {arg1 + arg2}
    val addCol = udf(code)
    danbaodf1.withColumn("rs", addCol(col("rs"), col("guar_name"))).show()
    duiwaidf2.filter(expr("length(cust_no) = 2 or length(cust_no) = 1")).show()
    duiwaidf2.filter(col("cust_no").contains("1")).show()
    duiwaidf2.filter(col("cust_no").notEqual("13")).show()
    val map = Map("cust_no" -> "0", "cust_name" -> "1")
    gudongdf4.na.fill(map).show()
    danbaodf1.join(duiwaidf2, danbaodf1("cust_no")===duiwaidf2("guar_no"), "left").show()
    danbaodf1.join(duiwaidf2, Seq("cust_no"), "inner").drop(duiwaidf2("cust_no")).show()
    danbaodf1.select("cust_no").rdd.map(row => row.getAs[String]("cust_no")).collect().foreach(str => println(str))
    gaoguandf3.groupBy("guar_no").agg(countDistinct("cust_no")).show()
    danbaodf1.filter(col("cust_no").isin("1", "5", "7")).show()
    danbaodf1.filter(!col("cust_no").isin(List(Array("1", "5", "7"):_*):_*)).show()

    danbaodf1.write.mode(saveMode = SaveMode.Overwrite)
      .format("csv").option("header", "true").option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .option("sep", "^").save("file:///Users/liudongfei/Downloads/csd-master/ProjectExamples/data/test")
    println("0,,".split(",").length)
  }

}
