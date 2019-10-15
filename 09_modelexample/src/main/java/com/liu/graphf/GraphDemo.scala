package com.liu.graphf

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{Column, SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.graphframes.GraphFrame
import org.graphframes.examples


/**
  * @Auther: liudongfei
  * @Date: 2019/1/9 13:58
  * @Description:
  */
object GraphDemo{
  def sumFriends(cnt: Column, relationship: Column): Column = {
    when(relationship === "friend", cnt + 1).otherwise(cnt)
  }

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    val spark = SparkSession
      .builder()
      .master("local")
      .appName("Graphs")
      .getOrCreate()
    val vertex = spark.createDataFrame(List(
      ("1","Jacob",48),
      ("2","Jessica",45),
      ("3","Andrew",25),
      ("4","Ryan",53),
      ("5","Emily",22),
      ("6","Lily",52)
    )).toDF("id", "name", "age")
    val edges = spark.createDataFrame(List(
      ("6","1","Sister"),
      ("1","2","Husband"),
      ("2","1","Wife"),
      ("5","1","Daughter"),
      ("5","2","Daughter"),
      ("3","1","Son"),
      ("3","2","Son"),
      ("4","1","Friend"),
      ("1","5","Father"),
      ("1","3","Father"),
      ("2","5","Mother"),
      ("2","3","Mother")
    )).toDF("src", "dst", "relationship")
    vertex.show()
//        vertex.withColumn("name", concat_ws("", lit("^"), col("name")))
//          .withColumn("age", concat_ws("", lit("^"), col("age")))
//          .write.mode(SaveMode.Overwrite).format("csv")
//          .option("sep", "!").option("header", "true")
//          .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
//          .save("file:///Users/liudongfei/edges")

    //构建图
//        val graph = GraphFrame(vertex, edges)
//        //打印图的顶点
//        graph.vertices.show()
//        //打印图的边
//        graph.edges.show()
//        //打印图中各个顶点的入度
//        graph.inDegrees.show()
//        //bfs算法，最短路径查询
//        graph.bfs.fromExpr("id = 1").toExpr("id = 5").maxPathLength(2).run().show(2)
//        //图中闭环三角的个数
//        graph.triangleCount.run().show()
//        val g: GraphFrame = examples.Graphs.friends
//        //模式查询
//        g.find("(a)-[e]->(b); (b)-[e2]->(a)").filter("b.age > 30").show()
//        val chain4 = g.find("(a)-[ab]->(b); (b)-[bc]->(c); (c)-[cd]->(d)")
//        val condition = {Seq("ab", "bc", "cd").foldLeft(lit(0))((cnt, e) => sumFriends(cnt, col(e)("relationship")))}
//        chain4.where(condition >= 2).show()
//        //连通分量算法
//        g.connectedComponents.setAlgorithm("graphx").run().show()
//        //强连通分量算法
//        g.stronglyConnectedComponents.maxIter(10).run().show()
//        //标签传播算法
//        g.labelPropagation.maxIter(5).run().show()
//        //pageRank算法
//        g.pageRank.resetProbability(0.15).tol(0.01).run().vertices.show()
//        g.pageRank.resetProbability(0.15).tol(0.01).run().edges.show()
//        //最短路径
//        g.shortestPaths.landmarks(Seq("a", "d")).run().show()
  }
}