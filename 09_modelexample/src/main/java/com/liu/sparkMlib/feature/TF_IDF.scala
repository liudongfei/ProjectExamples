package com.liu.sparkMlib.feature

import org.apache.spark.ml.feature.{HashingTF, IDF}
import org.apache.spark.sql.SparkSession

class TF_IDF {

}

object TF_IDF {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("tf-idf").getOrCreate()
    val sentenceData = spark.createDataFrame(Seq(
      (0.0, "Hi I heard about Spark"),
      (0.0, "I wish Java could use case classes"),
      (1.0, "Logistic regression models are neat")
    )).toDF("label", "sentence")

    val tokenizer = new org.apache.spark.ml.feature.Tokenizer().setInputCol("sentence").setOutputCol("words")

    val wordsData = tokenizer.transform(sentenceData)

    /**
      * TFIDF的主要思想是：如果某个词或短语在一篇文章中出现的频率TF高，并且在其他文章中很少出现，
      * 则认为此词或者短语具有很好的类别区分能力，适合用来分类。TFIDF实际上是：TF * IDF，TF词频(Term Frequency)，
      * IDF逆向文件频率(Inverse Document Frequency)。
      */
    val hashingTF = new HashingTF().setInputCol("words").setOutputCol("rawFeatures").setNumFeatures(20)
    val featurizedData = hashingTF.transform(wordsData)
    featurizedData.show()

    val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
    val idfModel = idf.fit(featurizedData)

    val rescaledData = idfModel.transform(featurizedData)
    rescaledData.select("label", "features").show()
  }
}