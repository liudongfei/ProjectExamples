package com.liu.sparkMlib.model.classification

import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.sql.SparkSession

/**
  * 朴素贝叶斯
  *
  */
class NaiveBayes {

}

object NaiveBayes {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("NaiveBayes").getOrCreate()
    val data = spark.read.format("libsvm").load("data/mllib/sample_libsvm_data.txt")
    val Array(train, test) = data.randomSplit(Array(0.7, 0.3), seed = 1234L)

    //创建朴素贝叶斯
    val model = new org.apache.spark.ml.classification.NaiveBayes().fit(train)

    val predictions = model.transform(test)

    predictions.show()

    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val accuracy = evaluator.evaluate(predictions)
    println("Test set accuracy = " + accuracy)
  }
}
