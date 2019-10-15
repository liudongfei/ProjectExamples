package com.liu.graphf

import com.liu.udf.StringPlus
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.graphframes.GraphFrame

/**
  * @Auther: liudongfei
  * @Date: 2019/1/9 14:06
  * @Description:关联关系图谱算法
  */
object RSGraph{
  /**
    * 验证身份证号正确性
    * @param certId
    * @return
    */
  private def checkCertId(certId : String): Boolean ={
    val arr=Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)
    val dicts = Map(0 -> '1', 1 -> '0', 2 -> 'X', 3 -> '9', 4 -> '8', 5 -> '7',
      6 -> '6', 7 -> '5', 8 -> '4', 9 ->'3', 10 -> '2')
    if(certId.length()!= 18){
      false
    } else {
      try{
        var checkVal = 0
        for (i <- 0 until 17){
          checkVal += arr(i) * Integer.valueOf(certId(i).toString())
        }
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
  private val func1 : (String => String) = (arg : String) => {arg.replaceAll("\\*", "")}
  private val udf1 = udf(func1)
  private val func2 : ((String, String) => String) = (arg1 : String, arg2 : String) => {
    if (arg2.contains("GR")) {
      if(arg1.length == 18){
        arg1
      } else {
        arg2
      }
    } else {
      arg2
    }
  }
  private val udf2 = udf(func2)
  private val func3 : ((String, String) => String) = (arg1 : String, arg2 : String) => {if(null == arg1) arg2 else arg1}
  private val udf3 = udf(func3)
  private val func4 : ((String, String) => String) = (arg1 : String, arg2 : String) => {if(checkCertId(arg1)) arg1 else arg2 }
  private val udf4 = udf(func4)
  private val func5 : (String => String) = (arg : String) => {arg.replaceAll("\\*", "投资客户名称")}
  private val udf5 = udf(func5)
  private val func6 : ((String, Int) => Int) = (arg1 : String, arg2 : Int) => {if(arg1.contains("类信贷")) arg2 else 0}
  private val udf6 = udf(func6)
  private val func7 : ((String, Int) => Int) = (arg1 : String, arg2 : Int) => {if(arg1.contains("类信贷")) 0 else arg2}
  private val udf7 = udf(func7)
  private val func8 : ((String, Int) => Int) = (arg1 : String, arg2 : Int) => {if(arg1.equals("垫款")) arg2 else 0}
  private val udf8 = udf(func8)
  private val func9 : ((Int, Int) => Int) = (arg1 : Int, arg2 : Int) => arg1 + arg2
  private val udf9 = udf(func9)
  private val func10 : ((String, Int) => Int) = (arg1 : String, arg2 : Int) => {if(arg1.equalsIgnoreCase("委托贷款")) 0 else arg2}
  private val udf10 = udf(func10)
  private val func11 :(String => Int) = (arg : String) => { if (arg.contains("+")) 1 else 0}
  private val udf11 = udf(func11)
  private val func12 :((String, String) => String) = (arg1 : String, arg2:String) => { if (null == arg1) arg2 else arg1}
  private val udf12 = udf(func12)
  private val func13 :((String) => Int) = (arg1 : String) => { if (arg1.length == 18 || arg1.contains("GR")) 0 else 1}
  private val udf13 = udf(func13)
  private val func16 : (String => String) = (arg : String) => {arg.replaceAll("总行帐务中心+", "").replaceAll("\\+", "")}
  private val udf16 = udf(func16)
  private val func17 :((String) => Int) = (arg1 : String) => { if (arg1.length == 18 || arg1.contains("GR")) 1 else 0}
  private val udf17 = udf(func17)
  private val func18 :((Int) => Int) = (arg1 : Int) => { if (arg1 > 1) 1 else 0}
  private val udf18 = udf(func18)

  def main(args: Array[String]): Unit = {
    val logger = Logger.getLogger("rsgraph")
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    val path1 = "hdfs:///user/liudongfei/spark/output/modelresult1"
    val path2 = "hdfs:///user/liudongfei/spark/output/modelresult2"
    val path3 = "hdfs:///user/liudongfei/spark/output/modelresult3"
    val spark = SparkSession.builder().appName("RSGraph").master("local").getOrCreate()
    spark.udf.register("stringPlus", new StringPlus)
    logger.info("读取数据。。。")

    val farenDF = spark.createDataFrame(List(
      ("12345678901", "abcd一"),
      ("12345678902", "abcd二"),
      ("12345678903", "abcd三"),
      ("12345678904", "abcd四"),
      ("12345678905", "abcd五"),
      ("12345678906", "abcd六"),
      ("12345678907", "abcd七"),
      ("12345678908", "abcd八"),
      ("12345678909", "abcd九"),
      ("123456789010", "abcd十"),
      ("123456789011", "abcd十一"),
      ("123456789012", "abcd十二"),
      ("123456789013", "abcd十三"),
      ("123456789014", "abcd十四"),
      ("123456789015", "abcd十五"),
      ("123456789016", "abcd十六"),
      ("123456789017", "abcd十七")
    )).toDF("customerid","enterprisename")
    val gaoguanDF = spark.createDataFrame(List(
      ("12345678903", "abcd三", "12345678904", "abcd四", "公司董事（关键人）", ""),
      ("123456789011", "abcd十一", "12345678907", "abcd七", "公司董事（关键人）", ""),
      ("123456789014", "abcd十四", "123456789015", "abcd十五", "公司董事（关键人）", "")
    )).toDF("cust_no","cust_name", "guar_no","guar_name", "rs", "guar_certid")
    val duiwaiDF = spark.createDataFrame(List(
      ("12345678902", "abcd二", "12345678903", "abcd三", "对外", ""),
      ("12345678905", "abcd五", "12345678901", "abcd一", "对外", ""),
      ("12345678908", "abcd八", "12345678909", "abcd九", "对外", ""),
      ("123456789010", "abcd十", "123456789011","abcd十一", "对外", ""),
      ("123456789013", "abcd十三", "123456789014", "abcd十四", "对外", ""),
      ("123456789015", "abcd十五", "123456789016", "abcd十六", "对外", "")
    )).toDF("cust_no","cust_name", "guar_no","guar_name", "rs", "guar_certid")
    val jituanDF = spark.createDataFrame(List(
      ("12345678904", "abcd四", "12345678905", "abcd五", "集团"),
      ("12345678909", "abcd九", "123456789011", "abcd十一", "集团"),
      ("123456789012", "abcd十二", "123456789013", "abcd十三", "集团"),
      ("123456789016", "abcd十六", "123456789012", "abcd十二", "集团")
    )).toDF("cust_no","cust_name", "guar_no","guar_name", "rs")
    val gudongDF = spark.createDataFrame(List(
      ("12345678904", "abcd四", "12345678905", "abcd五", "股东", ""),
      ("12345678909", "abcd九", "123456789011", "abcd十一", "股东", ""),
      ("123456789012", "abcd十二", "123456789013", "abcd十三", "股东", ""),
      ("123456789016", "abcd十六", "123456789012", "abcd十二", "股东", "")
    )).toDF("cust_no","cust_name", "guar_no","guar_name", "rs", "guar_certid")
    val danbaoDF = spark.createDataFrame(List(
      ("12345678901","abcd一", "12345678902", "abcd二", "担保", ""),
      ("12345678905","abcd五", "12345678906", "abcd六", "担保", ""),
      ("12345678907","abcd七", "12345678908", "abcd八", "担保", ""),
      ("12345678909","abcd九", "123456789010","abcd十", "担保", "")
    )).toDF("cust_no","cust_name", "guar_no","guar_name", "rs", "certid")

    val biaoneiDF = spark.createDataFrame(List(
      ("12345678901",  900, "类信贷", 0, 10, 0, 0, 0, "abc", 0),
      ("12345678902",  900, "类信贷", 0, 10, 0, 0, 0, "abc", 0),
      ("12345678903",  900, "类信贷", 0, 10, 0, 0, 0, "abc", 0),
      ("12345678904",  900, "垫款", 0, 10, 0, 0, 0, "cde", 0),
      ("12345678905",  900, "垫款", 0, 10, 0, 0, 0, "abd", 0),
      ("12345678906",  900, "垫款", 0, 10, 0, 0, 0, "abd", 0),
      ("12345678907",  900, "垫款", 0, 10, 0, 0, 0, "cde", 0),
      ("12345678908",  900, "类信贷", 0, 10, 0, 0, 0, "abc", 0),
      ("12345678909",  900, "类信贷", 0, 10, 0, 0, 0, "abc", 0),
      ("123456789010", 900, "类信贷", 0, 10, 0, 0, 0, "abc", 0),
      ("123456789011", 900, "类信贷", 0, 10, 0, 0, 0, "cde", 0),
      ("123456789012", 900, "类信贷", 0, 10, 0, 0, 0, "abd", 0),
      ("123456789013", 900, "类信贷", 0, 10, 0, 0, 0, "abd", 0),
      ("123456789014", 900, "委托贷款", 0, 10, 0, 0, 0, "abc", 0),
      ("123456789015", 900, "委托贷款", 0, 10, 0, 0, 0, "cde", 0),
      ("123456789016", 900, "委托贷款", 0, 10, 0, 0, 0, "cde", 0),
      ("123456789017", 900, "委托贷款", 0, 10, 0, 0, 0, "cde", 0)
    )).toDF("customerid","subjectno", "businessname", "balance1",
      "overduedays", "overduebalance", "interestbalance1", "interestbalance2", "fhjg", "oweinterestdays")
    val biaowaiDF = spark.createDataFrame(List(
      ("12345678901",  "类信贷", 0),
      ("12345678902",  "类信贷", 0),
      ("12345678903",  "类信贷", 0),
      ("12345678904",  "垫款", 0),
      ("12345678905",  "垫款", 0),
      ("12345678906",  "垫款", 0),
      ("12345678907",  "垫款", 0),
      ("12345678908",  "类信贷", 0),
      ("12345678909",  "类信贷", 0),
      ("123456789010", "类信贷", 0),
      ("123456789011", "类信贷", 0),
      ("123456789012", "类信贷", 0),
      ("123456789013", "类信贷", 0),
      ("123456789014", "委托贷款", 0),
      ("123456789015", "委托贷款", 0),
      ("123456789016", "委托贷款", 0),
      ("123456789017", "委托贷款", 0)
    )).toDF("customerid","businessname", "ckou")
    val weiyueDF = spark.createDataFrame(List(("", "")
    )).toDF("id","cust_no")

    logger.info("进行数据预处理。。。")
    logger.info("法人信息预处理")
    val farenDF1 = farenDF.select("customerid", "enterprisename")
      .withColumn("enterprisename", udf1(col("enterprisename")))//对客户名称进行处理，'*' 替换为 ''
      .dropDuplicates() //去除重复
      .groupBy("enterprisename")
      .agg(expr("stringPlus(customerid) as customerid"))
      .filter(!col("customerid").contains("+"))
      .filter(col("enterprisename").notEqual(""))
      .cache()
    farenDF1.show()

    logger.info("担保信息预处理")
    val danbaoDF1 = danbaoDF.select("cust_no", "cust_name", "guar_no", "guar_name", "rs", "certid")
      .filter(expr("length(cust_no) = 10 or length(cust_no) = 11"))
      .filter(!col("guar_no").contains("BACNSPDB"))
      .dropDuplicates()
      .withColumn("guar_no", udf2(col("certid"), col("guar_no"))) //对于合法的certid，替换guar_no
      .select("cust_no", "cust_name", "guar_no", "guar_name", "rs")
      .withColumn("rs", lit("担保"))
      .dropDuplicates("cust_name", "guar_name")
      .cache()
    danbaoDF1.show()

    logger.info("股东信息预处理")
    val gudongDF1 = gudongDF.withColumn("cust_name", udf1(col("cust_name")))
      .join(farenDF1.withColumnRenamed("enterprisename", "cust_name"), Seq("cust_name"), "left") //使用法人信息表做交叉验证
      .withColumn("cust_no", udf3(col("customerid"), col("cust_no")))
      .drop("customerid")
      .join(farenDF1.withColumnRenamed("customerid","cust_no"), Seq("cust_no"), "left")
      .withColumn("cust_name", udf3(col("enterprisename"), col("cust_name")))
      .drop("enterpriseName")
      .withColumn("guar_no", udf4(col("guar_certid"), col("guar_no"))) //使用合法的guar_certid替换guar_no
      .filter(!col("cust_no").contains("RL"))
      .filter(!col("guar_no").contains("RL"))
      .filter(expr("length(cust_name) >= 4 or length(guar_name) >= 4"))
      .withColumn("guar_no", udf2(col("guar_certid"), col("guar_no")))
      .select("cust_no", "cust_name", "guar_no", "guar_name", "rs")
      .withColumn("rs", lit("股东"))
      .dropDuplicates("cust_name", "guar_name")
      .cache()
    gudongDF1.show()

    logger.info("对外关系预处理")
    val duiwaiDF1 = duiwaiDF.withColumn("cust_name", udf1(col("cust_name")))
      .withColumn("guar_name", udf5(col("guar_name")))
      .join(farenDF1.withColumnRenamed("enterprisename", "cust_name"), Seq("cust_name"), "left") //使用法人信息表做交叉验证
      .withColumn("cust_no", udf3(col("customerid"), col("cust_no")))
      .drop("customerid")
      .join(farenDF1.withColumnRenamed("customerid","cust_no"), Seq("cust_no"), "left")
      .withColumn("cust_name", udf3(col("enterprisename"), col("cust_name")))
      .drop("enterpriseName")
      .withColumn("guar_no", udf4(col("guar_certid"), col("guar_no")))
      .filter(!col("cust_no").contains("RL"))
      .filter(!col("guar_no").contains("RL"))
      .filter(expr("length(cust_name) >= 4 or length(guar_name) >= 4"))
      .select("cust_no", "cust_name", "guar_no", "guar_name", "rs")
      .withColumn("rs", lit("对外股权"))
      .dropDuplicates("cust_name", "guar_name")
      .cache()
    duiwaiDF1.show()

    logger.info("集团信息预处理")
    val jituanDF1 = jituanDF.withColumn("cust_name", udf1(col("cust_name")))
      .withColumn("guar_name", udf1(col("guar_name")))
      .join(farenDF1.withColumnRenamed("enterprisename", "cust_name"), Seq("cust_name"), "left") //使用法人信息表做交叉验证
      .withColumn("cust_no", udf3(col("customerid"), col("cust_no")))
      .drop("customerid")
      .join(farenDF1.withColumnRenamed("customerid","cust_no"), Seq("cust_no"), "left")
      .withColumn("cust_name", udf3(col("enterprisename"), col("cust_name")))
      .drop("enterpriseName")
      .select("cust_no", "cust_name", "guar_no", "guar_name", "rs")
      .withColumn("rs", lit("集团"))
      .dropDuplicates("cust_name", "guar_name")
      .cache()
    jituanDF1.show()

    logger.info("高管关系预处理")
    val gaoguanDF1 = gaoguanDF.withColumn("cust_name", udf1(col("cust_name")))
      .filter(col("cust_name").notEqual(""))
      .filter(col("rs").isin("公司董事（关键人）", "法人代表（关键人）", "实际控股人（关键人）"))
      //      .filter(expr("length(guar_no) = 18 and certtype = '110C'"))
      .dropDuplicates()
      .join(farenDF1.withColumnRenamed("enterprisename", "cust_name"), Seq("cust_name"), "left") //使用法人信息表做交叉验证
      .withColumn("cust_no", udf3(col("customerid"), col("cust_no")))
      .drop("customerid")
      .join(farenDF1.withColumnRenamed("customerid","cust_no"), Seq("cust_no"), "left")
      .withColumn("cust_name", udf3(col("enterprisename"), col("cust_name")))
      .drop("enterpriseName")
      .withColumn("guar_no", udf4(col("guar_certid"), col("guar_no")))
      .filter(!col("cust_no").contains("RL"))
      .filter(!col("guar_no").contains("RL"))
      .filter(expr("length(cust_name) >= 4 or length(guar_name) >= 4"))
      .select("cust_no", "cust_name", "guar_no", "guar_name", "rs")
      .withColumn("rs", lit("高管"))
      .dropDuplicates("cust_name", "guar_name")
      .cache()
    gaoguanDF1.show()

    logger.info("关系数据汇总")
    var relationshipDF = danbaoDF1.union(duiwaiDF1).union(gaoguanDF1).union(gudongDF1).union(jituanDF1)
      .filter(!col("cust_no").isin("2000000000", "2000200465"))
      .filter(!col("guar_no").isin("2000000000", "2000200465"))
      .filter(col("cust_no").notEqual(col("guar_no")))
      .dropDuplicates("cust_no", "guar_no", "rs")
      .dropDuplicates("cust_name", "guar_name", "rs")
      .cache()
    relationshipDF.show()

    val custNoDropList = relationshipDF.groupBy("cust_no")
      .agg(countDistinct("cust_name").as("countdis")).filter(expr("countdis > 1"))
      .select("cust_no").rdd.map(row => row.getAs[String]("cust_no")).collect()
    println(List(custNoDropList:_*))
    val custNameoDropList = relationshipDF.groupBy("cust_name")
      .agg(countDistinct("cust_no").as("countdis")).filter(expr("countdis > 1"))
      .select("cust_name").rdd.map(row => row.getAs[String]("cust_name")).collect()
    println(List(custNameoDropList:_*))
    val guarNoDropList = relationshipDF.groupBy("guar_no")
      .agg(countDistinct("guar_name").as("countdis")).filter(expr("countdis > 1"))
      .select("guar_no").rdd.map(row => row.getAs[String]("guar_no")).collect()
    println(List(guarNoDropList:_*))
    val guarNameDropList = relationshipDF.groupBy("guar_name")
      .agg(countDistinct("guar_no").as("countdis")).filter(expr("countdis > 1"))
      .select("guar_name").rdd.map(row => row.getAs[String]("guar_name")).collect()
    println(List(guarNameDropList:_*))
    relationshipDF = relationshipDF
      .filter(!col("cust_no").isin(List(custNoDropList:_*):_*))
      .filter(!col("cust_name").isin(List(custNameoDropList:_*):_*))
      .filter(!col("guar_no").isin(List(guarNoDropList:_*):_*))
      .filter(!col("guar_name").isin(List(guarNameDropList:_*):_*))
      .cache()
    relationshipDF.show()
    //***********************************************

    logger.info("表内数据预处理")
    val biaoneiDF1 = biaoneiDF
      .filter(!col("subjectno").isin(927, 967))
      .withColumn("lxd_amount", udf6(col("businessname"), col("balance1")))
      .withColumn("balance1", udf7(col("businessname"), col("balance1")))
      .withColumn("amt_in", col("balance1"))
      .withColumn("diankuan", udf8(col("businessname"), col("balance1")))
      .withColumn("yuqi_amount", col("overduebalance"))
      .withColumn("qianxi", udf9(col("interestbalance1"), col("interestbalance2")))
      .groupBy("customerid")
      .agg(sum("balance1").as("balance1"),
        sum("overduebalance").as("overduebalance"),
        sum("interestbalance1").as("interestbalance1"),
        sum("interestbalance2").as("interestbalance2"),
        sum("lxd_amount").as("lxd_amount"),
        sum("amt_in").as("amt_in"),
        sum("diankuan").as("diankuan"),
        sum("yuqi_amount").as("yuqi_amount"),
        sum("qianxi").as("qianxi"),
        max("subjectno").as("subjectno"),
        max("businessname").as("businessname"),
        max("fhjg").as("fhjg"),
        max("oweinterestdays").as("oweinterestdays"),
        max("overduedays").as("overduedays"))
      .cache()
    biaoneiDF.show()


    logger.info("表外数据预处理")
    val biaowaiDF1 = biaowaiDF.withColumn("ckou", udf10(col("businessname"), col("ckou")))
      .groupBy("customerid")
      .agg(sum("ckou").as("ckou"))
      .cache()
    biaowaiDF1.show()

    logger.info("数据预处理完成")
    logger.info("构建大图")
    val edges = relationshipDF.select("cust_no", "guar_no")
      .withColumnRenamed("cust_no", "src")
      .withColumnRenamed("guar_no", "dst")
    logger.info("拆分子图")
    val subGraphInfo = GraphFrame.fromEdges(edges)
      .connectedComponents
      .setAlgorithm("graphx")
      .run().select("id", "component")
      .withColumnRenamed("id", "cust_no")
      .withColumnRenamed("component", "circle_id")
    subGraphInfo.show(100)

    logger.info("各个子圈中各条边的关系汇总")
    val circleInfo = relationshipDF.groupBy("cust_no", "guar_no", "cust_name", "guar_name")
      .agg(expr("stringPlus(rs) as rs"))
      .join(subGraphInfo,Seq("cust_no"), "left")
      .withColumn("model_id", lit("Model_5"))
      .withColumn("model_date", current_date())
      .withColumnRenamed("cust_no", "to_id")
      .withColumnRenamed("cust_name", "to_name")
      .withColumnRenamed("guar_no", "from_id")
      .withColumnRenamed("guar_name", "from_name")
      .withColumnRenamed("rs", "relations")
      .na.fill(0)
    logger.info("保存结果表1！！！！")
    circleInfo
      .select("model_id", "circle_id", "from_id", "from_name", "to_id", "to_name", "relations", "model_date")
      .withColumn("circle_id", concat_ws("", lit("^"), col("circle_id")))
      .withColumn("from_id", concat_ws("", lit("^"), col("from_id")))
      .withColumn("from_name", concat_ws("", lit("^"), col("from_name")))
      .withColumn("to_id", concat_ws("", lit("^"), col("to_id")))
      .withColumn("to_name", concat_ws("", lit("^"), col("to_name")))
      .withColumn("relations", concat_ws("", lit("^"), col("relations")))
      .withColumn("model_date", concat_ws("", lit("^"), col("model_date")))
      .write.mode(SaveMode.Overwrite)
      .format("csv")
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .option("header", "true").option("sep", "!").save(path1)


    logger.info("开始统计子图中每个客户层面的信息")
    logger.info("子图中每个客户编号所属子图客户编号的数量汇总")
    val res_nodnum = subGraphInfo
      .groupBy("circle_id").agg(count("cust_no").as("nod_num"))
    res_nodnum.show()

    logger.info("子图中集团客户的数量汇总")
    val res_jituannum = subGraphInfo.join(jituanDF1, Seq("cust_no"), "left")
      .groupBy("circle_id")
      .agg(count("cust_no").as("jituan_num"))
    res_jituannum.show()

    logger.info("子图中关联关系汇总及是否含多种关系")
    val res_relations = subGraphInfo
      .join(relationshipDF.select("cust_no", "rs"), Seq("cust_no"), "left")
      .na.drop()
      .groupBy("circle_id")
      .agg(expr("stringPlus(rs) as rs"))
      .withColumn("if_cross_relation", udf11(col("rs")))
      .withColumnRenamed("rs", "relations")
    res_relations.show()

    logger.info("汇总子图中客户层面的信息")
    val res = subGraphInfo
      .join(res_nodnum, Seq("circle_id"), "left")
      .join(res_jituannum, Seq("circle_id"), "left")
      .join(res_relations, Seq("circle_id"), "left")
      .join(relationshipDF.select("cust_no", "cust_name"), Seq("cust_no"), "left")
      .join(relationshipDF.select("guar_no", "guar_name")
        .withColumnRenamed("guar_no", "cust_no"), Seq("cust_no"), "left")
      .withColumn("customername", udf12(col("cust_name"), col("guar_name")))
      .drop("cust_name", "guar_name")
      .withColumn("model_id", lit("Model_5"))
      .withColumn("model_date", current_date())
      .withColumnRenamed("cust_no", "customerid")
      .na.fill(0)
      .dropDuplicates()
    logger.info("保存结果表2！！！！")
    res.select("model_id", "customerid", "customername", "circle_id", "nod_num", "jituan_num", "relations",
      "if_cross_relation", "model_date")
      .withColumn("customerid", concat_ws("", lit("^"), col("customerid")))
      .withColumn("customername", concat_ws("", lit("^"), col("customername")))
      .withColumn("circle_id", concat_ws("", lit("^"), col("circle_id")))
      .withColumn("nod_num", concat_ws("", lit("^"), col("nod_num")))
      .withColumn("jituan_num", concat_ws("", lit("^"), col("jituan_num")))
      .withColumn("relations", concat_ws("", lit("^"), col("relations")))
      .withColumn("if_cross_relation", concat_ws("", lit("^"), col("if_cross_relation")))
      .withColumn("model_date", concat_ws("", lit("^"), col("model_date")))
      .write.mode(SaveMode.Overwrite)
      .format("csv")
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .option("header", "true").option("sep", "!").save(path2)


    logger.info("开始统计各个子图层面的信息")
    val customerList = farenDF1.select("customerid").rdd.map(row => row.getAs[String]("customerid")).collect()
    val func14: ((String) => Int) = (arg1: String) => {
      if (customerList.contains(arg1) && arg1.contains("Gu") && arg1.contains("duiwai")) 1 else 0
    }
    val udf14 = udf(func14)
    val idList = weiyueDF.select("id").rdd.map(row => row.getAs[String]("id")).collect()
    val func15: ((String) => Int) = (arg1: String) => {if (idList.contains(arg1)) 1 else 0}
    val udf15 = udf(func15)

    logger.info("保存结果表3！！！！")
    subGraphInfo.withColumnRenamed("cust_no", "customerid")
      .withColumn("company_num", udf13(col("customerid")))
      .withColumn("individual_num", udf17(col("customerid")))
      .withColumn("fwh_num", udf14(col("customerid")))
      .join(biaoneiDF1, Seq("customerid"), "left")
      .join(biaowaiDF1, Seq("customerid"), "left")
      .withColumn("default_num", udf15(col("customerid")))
      .withColumn("fhjg", udf16(col("fhjg")))
      .groupBy("circle_id")
      .agg(count("company_num").as("company_num"),
        sum("individual_num").as("individual_num"),
        sum("fwh_num").as("fwh_num"),
        sum("amt_in").as("amt_in"),
        sum("diankuan").as("diankuan"),
        max("overduedays").as("biggest_yuqi_days"),
        sum("qianxi").as("qianxi"),
        max("oweinterestdays").as("biggest_qianxi_days"),
        sum("lxd_amount").as("lxd_amount"),
        sum("ckou").as("ckou"),
        countDistinct("fhjg").as("if_cross_bank"),
        expr("stringPlus(fhjg) as related_branch"),
        sum("default_num").as("default_num"))
      .withColumn("if_cross_bank", udf18(col("if_cross_bank")))
      .join(res_nodnum, Seq("circle_id"), "left")
      .join(res_jituannum, Seq("circle_id"), "left")
      .join(res_relations, Seq("circle_id"), "left")
      .withColumn("model_id", lit("Model_5"))
      .withColumn("model_date", current_date())
      .na.fill(0)
      .select("model_id", "circle_id", "nod_num", "jituan_num", "relations", "if_cross_relation",
        "company_num", "individual_num", "fwh_num", "amt_in", "diankuan", "biggest_yuqi_days", "qianxi",
        "biggest_qianxi_days", "lxd_amount", "ckou", "if_cross_bank", "related_branch", "default_num", "model_date")
      .withColumn("circle_id", concat_ws("", lit("^"), col("circle_id")))
      .withColumn("nod_num", concat_ws("", lit("^"), col("nod_num")))
      .withColumn("jituan_num", concat_ws("", lit("^"), col("jituan_num")))
      .withColumn("relations", concat_ws("", lit("^"), col("relations")))
      .withColumn("if_cross_relation", concat_ws("", lit("^"), col("if_cross_relation")))
      .withColumn("company_num", concat_ws("", lit("^"), col("company_num")))
      .withColumn("individual_num", concat_ws("", lit("^"), col("individual_num")))
      .withColumn("fwh_num", concat_ws("", lit("^"), col("fwh_num")))
      .withColumn("amt_in", concat_ws("", lit("^"), col("amt_in")))
      .withColumn("diankuan", concat_ws("", lit("^"), col("diankuan")))
      .withColumn("biggest_yuqi_days", concat_ws("", lit("^"), col("biggest_yuqi_days")))
      .withColumn("qianxi", concat_ws("", lit("^"), col("qianxi")))
      .withColumn("biggest_qianxi_days", concat_ws("", lit("^"), col("biggest_qianxi_days")))
      .withColumn("lxd_amount", concat_ws("", lit("^"), col("lxd_amount")))
      .withColumn("ckou", concat_ws("", lit("^"), col("ckou")))
      .withColumn("if_cross_bank", concat_ws("", lit("^"), col("if_cross_bank")))
      .withColumn("related_branch", concat_ws("", lit("^"), col("related_branch")))
      .withColumn("default_num", concat_ws("", lit("^"), col("default_num")))
      .withColumn("model_date", concat_ws("", lit("^"), col("model_date")))
      .write.mode(SaveMode.Overwrite)
      .format("csv")
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .option("header", "true").option("sep", "!").save(path3)
    logger.info("图谱模型运行成功")
  }
}