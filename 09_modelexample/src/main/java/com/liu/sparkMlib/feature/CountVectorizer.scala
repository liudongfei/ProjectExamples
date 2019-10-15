package com.liu.sparkMlib.feature

import org.apache.spark.ml.feature.CountVectorizerModel
import org.apache.spark.sql.SparkSession

class CountVectorizer {

}

object CountVectorizer {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("countVector").getOrCreate()
    val df = spark.createDataFrame(Seq(
      (0, Array("a", "b", "c")),
      (1, Array("a", "b", "b", "c", "a"))
    )).toDF("id", "words")

    val cvModel: CountVectorizerModel = new org.apache.spark.ml.feature.CountVectorizer()
      .setInputCol("words")
      .setOutputCol("features")
      .setVocabSize(3)
      .setMinDF(2)
      .fit(df)

    val cvm = new CountVectorizerModel(Array("a", "b", "c"))
      .setInputCol("words")
      .setOutputCol("features")

    cvModel.transform(df).show(false)
  }
}