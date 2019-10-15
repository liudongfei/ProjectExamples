import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
/**
  * @Auther: liudongfei
  * @Date: 2019/2/15 20:45
  * @Description:
  */
class test {

}

object test{
  def readCsv(): Unit ={
    val spark = SparkSession.builder().master("local").appName("test").getOrCreate()
    val df1 = spark
      .read
      .option("header", "true")
      .option("delimiter", ",")
      .csv("file:///Users/liudongfei/Myworkspace/workspace/java_workspace/ProjectExamples/data/cars.csv")
    df1.show()
    df1.orderBy(df1("year").desc).show()
//    df1.toDF("1", "2", "3", "4", "5").show()
    df1.groupBy(col("year")).agg(count(col("year")))
      .repartition(1).write.format("csv")
      .option("header", "true")
      .option("delimiter", "\t")
      .save("file:///Users/liudongfei/Myworkspace/workspace/java_workspace/ProjectExamples/data/cars")
//    df1.filter(col("model").equalTo("S")).show()
//    df1.filter(col("model").notEqual("S")).orderBy(col("year").desc)
//      .rdd.map(_.toSeq.map(_+"").reduce(_ + "~~~" + _))
//      .repartition(1)
//      .saveAsTextFile("file:///Users/liudongfei/Myworkspace/workspace/java_workspace/ProjectExamples/data/cars.txt")

  }
  def readTextFile(): Unit ={
    val spark = SparkSession.builder().master("local").appName("test").getOrCreate()
    val rdd = spark
      .read
      .textFile("file:///Users/liudongfei/Myworkspace/workspace/java_workspace/ProjectExamples/data/students.txt")
    rdd.show()
  }
  def readJson(): Unit ={
    val spark = SparkSession.builder().master("local").appName("test").getOrCreate()
    val df = spark.read.json("file:///Users/liudongfei/Myworkspace/workspace/java_workspace/ProjectExamples/data/students.json")
    df.show()
  }
  def readHive(): Unit ={
    val spark = SparkSession.builder().master("local").appName("test").enableHiveSupport().getOrCreate()
    spark.table("employees.employees").write.saveAsTable("employees.employees_spark")
  }

  def main(args: Array[String]): Unit = {
//    readTextFile()
//    readJson()
//    readCsv()
    readHive()
  }
}