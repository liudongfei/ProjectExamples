package com.liu.sparkMlib.model.classification

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{GBTClassificationModel, GBTClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer}
import org.apache.spark.sql.SparkSession

class GBTModel {

}

object GBTModel {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName("GBTModel").getOrCreate()
    val data = spark.read.format("libsvm").load("data/sample_libsvm_data.txt")
    val labelIndexer = new StringIndexer()
      .setInputCol("label")
      .setOutputCol("indexedLabel")
      .fit(data)
    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .setMaxCategories(4)
      .fit(data)
    val Array(trainingData, testData) = data.randomSplit(Array(0.7, 0.3))

    //创建迭代决策树模型
    val gbt = new GBTClassifier()
      .setLabelCol("indexedLabel")
      .setFeaturesCol("indexedFeatures")
      .setMaxIter(10)
    val labelConverter = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("predictedLabel")
      .setLabels(labelIndexer.labels)

    val pipeline = new Pipeline()
      .setStages(Array(labelIndexer, featureIndexer, gbt, labelConverter))
    val model = pipeline.fit(trainingData)
    val predictions = model.transform(testData)
    predictions.select("predictedLabel", "label", "features").show(5)

    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("indexedLabel")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val accuracy = evaluator.evaluate(predictions)
    println("Test Error = " + (1.0 - accuracy))
    val gbtModel = model.stages(2).asInstanceOf[GBTClassificationModel]
    println("Learned classification GBT model:\n" + gbtModel.toDebugString)


  }
}