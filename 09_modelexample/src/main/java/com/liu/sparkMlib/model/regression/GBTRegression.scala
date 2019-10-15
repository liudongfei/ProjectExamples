package com.liu.sparkMlib.model.regression

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.feature.VectorIndexer
import org.apache.spark.ml.regression.GBTRegressionModel
import org.apache.spark.sql.SparkSession

class GBTRegression {

}

object GBTRegression {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("GBTRegression").getOrCreate()
    val data = spark.read.format("libsvm").load("data/mllib/sample_libsvm_data.txt")
    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .setMaxCategories(4)
      .fit(data)

    // Split the data into training and test sets (30% held out for testing).
    val Array(trainingData, testData) = data.randomSplit(Array(0.7, 0.3))

    val gbt = new org.apache.spark.ml.regression.GBTRegressor()
      .setLabelCol("label")
      .setFeaturesCol("indexedFeatures")
      .setMaxIter(10)

    // Chain indexer and GBT in a Pipeline.
    val pipeline = new Pipeline()
      .setStages(Array(featureIndexer, gbt))

    // Train model. This also runs the indexer.
    val model = pipeline.fit(trainingData)

    // Make predictions.
    val predictions = model.transform(testData)

    // Select example rows to display.
    predictions.select("prediction", "label", "features").show(5)
    val evaluator = new RegressionEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("rmse")
    val rmse = evaluator.evaluate(predictions)
    println("Root Mean Squared Error (RMSE) on test data = " + rmse)

    val gbtModel = model.stages(1).asInstanceOf[GBTRegressionModel]
    println("Learned regression GBT model:\n" + gbtModel.toDebugString)
  }
}
