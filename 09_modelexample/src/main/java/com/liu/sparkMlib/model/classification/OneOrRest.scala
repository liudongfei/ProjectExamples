package com.liu.sparkMlib.model.classification

import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.sql.SparkSession

class OneOrRest {

}

object OneOrRest {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("OneOrRest").getOrCreate()
    val data = spark.read.format("libsvm").load("data/mllib/sample_multiclass_classification_data.txt")

    val Array(train, test) = data.randomSplit(Array(0.7, 0.3))
    val classifier = new org.apache.spark.ml.classification.LogisticRegression()
      .setMaxIter(10)
      .setTol(1E6)
      .setFitIntercept(true)

    val ovr = new org.apache.spark.ml.classification.OneVsRest()
      .setClassifier(classifier)
    val ovrModel = ovr.fit(train)

    val predictions = ovrModel.transform(test)

    val evaluator = new MulticlassClassificationEvaluator().setMetricName("accuracy")
    val accuracy = evaluator.evaluate(predictions)

    println("model accuracy:\t" + accuracy)
  }
}
