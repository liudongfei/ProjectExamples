package com.liu.sparkMlib.model.regression

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.feature.VectorIndexer
import org.apache.spark.ml.regression.DecisionTreeRegressionModel
import org.apache.spark.sql.SparkSession

/**
  * 决策树回归
  */
class DecisionTree {

}

object DecisionTree {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("decisiontree").getOrCreate()
    val data = spark.read.format("libsvm").load("data/mllib/sample_libsvm_data.txt")


    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .setMaxCategories(4)
      .fit(data)

    val Array(train, test) = data.randomSplit(Array(0.7,0.3))

    //创建决策树回归模型
    val dt = new org.apache.spark.ml.regression.DecisionTreeRegressor()
      .setLabelCol("label")
      .setFeaturesCol("indexedFeatures")

    val pipline = new Pipeline().setStages(Array(featureIndexer, dt))

    val model = pipline.fit(train)

    val predictions = model.transform(test)

    predictions.select("prediction", "label", "features").show(5)

    val evaluator = new RegressionEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("rmse")
    val rmse = evaluator.evaluate(predictions)

    println("Root Mean Squared Error (RMSE) on test data = " + rmse)

    val treeModel = model.stages(1).asInstanceOf[DecisionTreeRegressionModel]
    println("Learned regression tree model:\n" + treeModel.toDebugString)

  }
}
