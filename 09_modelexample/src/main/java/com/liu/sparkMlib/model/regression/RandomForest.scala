package com.liu.sparkMlib.model.regression

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.feature.VectorIndexer
import org.apache.spark.ml.regression.RandomForestRegressionModel
import org.apache.spark.sql.SparkSession

class RandomForest {

}

object RandomForest {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("randomforest").getOrCreate()
    val data = spark.read.format("libsvm").load("data/mllib/sample_libsvm_data.txt")
    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .setMaxCategories(4)
      .fit(data)

    val Array(train, test) = data.randomSplit(Array(0.7, 0.3))
    val rf = new org.apache.spark.ml.regression.RandomForestRegressor()
      .setLabelCol("label")
      .setFeaturesCol("indexedFeatures")

    val pipline = new Pipeline().setStages(Array(featureIndexer, rf))

    val model = pipline.fit(train)

    val predictions = model.transform(test)

    predictions.select("prediction", "label", "features").show()

    val evaluator = new RegressionEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("rmse")
    val rmse = evaluator.evaluate(predictions)
    println("Root Mean Squared Error (RMSE) on test data = " + rmse)

    val rfModel = model.stages(1).asInstanceOf[RandomForestRegressionModel]
    println("Learned regression forest model:\n" + rfModel.toDebugString)
  }
}
