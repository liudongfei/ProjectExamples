import org.apache.spark.{SparkConf, SparkContext}

/**
  * @Auther: liudongfei
  * @Date: 2019/2/16 12:57
  * @Description:
  */
object test2 {
  def main (args : Array[String]): Unit ={
    val conf = new SparkConf().setMaster("local").setAppName("test2")
    val sc = new SparkContext(conf)
    val rdd = sc.textFile("file:///Users/liudongfei/zookeeper.out")
    val rdd2 = rdd.flatMap(s => s.split(" "))
    val pairrdd = rdd2.map(s => (s, 1))
    pairrdd.reduceByKey(_ + _)
      .filter(s => !s._1.contains("-"))
      .map(t => (t._2, t._1)).sortByKey(false).map(t => (t._2, t._1)).foreach(println(_))

  }
}
