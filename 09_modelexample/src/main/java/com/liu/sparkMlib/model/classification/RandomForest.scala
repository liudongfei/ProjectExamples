package com.liu.sparkMlib.model.classification

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.RandomForestClassificationModel
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer}
import org.apache.spark.sql.SparkSession


/**
  * 随机森林
  *
  */
class RandomForest {

}

object RandomForest {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("RandomForest").getOrCreate()
    val  data = spark.read.format("libsvm").load("data/sample_libsvm_data.txt")
    data.show(10)
    //对label建立类别特征索引
    val labelIndexer = new StringIndexer()
      .setInputCol("label")
      .setOutputCol("indexedLabel")
      .fit(data)
    labelIndexer.transform(data).show(10)
    labelIndexer.labels.foreach(x => print(x))


    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .setMaxCategories(4)
      .fit(data)
    featureIndexer.transform(data).show()

    val Array(trainingData, testData) = data.randomSplit(Array(0.7, 0.3))
    //创建随机森林模型
    val rf = new org.apache.spark.ml.classification.RandomForestClassifier()
      .setLabelCol("indexedLabel")
      .setFeaturesCol("indexedFeatures")
      .setNumTrees(10)

    val labelConverter = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("predictedLabel")
      .setLabels(labelIndexer.labels)

    val pipline = new Pipeline()
      .setStages(Array(labelIndexer, featureIndexer, rf, labelConverter))

    val model = pipline.fit(trainingData)

    val predictions = model.transform(testData)

    predictions.select("predictedLabel", "label", "features").show(10)

    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("indexedLabel")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")

    val accuracy = evaluator.evaluate(predictions)

    println("testError = " + (1.0 -accuracy))
    val rfModel = model.stages(2).asInstanceOf[RandomForestClassificationModel]
    println("Learned classification forest model:\n" + rfModel.toDebugString)
  }
}