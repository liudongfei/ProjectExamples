package com.liu.sparkMlib.feature

import org.apache.spark.sql.{Row, SparkSession}

class Word2Vec {

}

object Word2Vec {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("word2vec").getOrCreate()
    val documentDF = spark.createDataFrame(Seq(
      "Hi I heard about Spark".split(" "),
      "I wish Java could use case classes".split(" "),
      "Logistic regression models are neat".split(" ")
    ).map(Tuple1.apply)).toDF("text")
    documentDF.show()
    /**
      * word2vec 通过训练，可以把对文本内容的处理简化为 K 维向量空间中的向量运算，而向量空间上的相似度可以用来表示文本语义上的相似度
      */
    val word2Vec = new org.apache.spark.ml.feature.Word2Vec()
      .setInputCol("text")
      .setOutputCol("result")
      .setVectorSize(3)
      .setMinCount(0)
    val model = word2Vec.fit(documentDF)

    val result = model.transform(documentDF)

    result.collect().foreach { case Row(text: Seq[_], features: org.apache.spark.ml.linalg.Vector) =>
      println(s"Text: [${text.mkString(", ")}] => \nVector: $features\n") }
  }
}