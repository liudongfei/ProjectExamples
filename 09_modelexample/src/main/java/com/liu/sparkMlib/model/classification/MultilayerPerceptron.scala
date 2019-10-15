package com.liu.sparkMlib.model.classification

import org.apache.spark.ml.classification.MultilayerPerceptronClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.sql.SparkSession

/**
  * 神经网络
  *
  */
class MultilayerPerceptron {

}

object MultilayerPerceptron {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("MuiltilayerPerceptrn").getOrCreate()
    val data = spark.read.format("libsvm").load("data/mllib/sample_multiclass_classification_data.txt")
    val splits = data.randomSplit(Array(0.6, 0.4), seed = 1234L)
    val train = splits(0)
    val test = splits(1)

    //中间层
    val layer = Array[Int](4, 5, 3)


    //创建神经网络模型
    val modelTrain = new MultilayerPerceptronClassifier()
      .setLayers(layer)
      .setBlockSize(128)
      .setSeed(1234L)
      .setMaxIter(10)

    val model = modelTrain.fit(train)

    val result = model.transform(test)

    val predictionAndLabel = result.select("prediction", "label")
    predictionAndLabel.show()

    val evaluator = new MulticlassClassificationEvaluator().setMetricName("accuracy")

    val accuracy = evaluator.evaluate(predictionAndLabel)

    println("model accuracy:\t" + accuracy)

  }
}