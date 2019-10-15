package com.liu.sparkMlib.model.classification

import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.DecisionTreeClassificationModel
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer}
import org.apache.spark.sql.SparkSession

/**
  * 决策树
  *
  * 1、
  */
class DecisionTree {

}

object DecisionTree {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)

    val spark = SparkSession.builder().master("local").appName("DecisonTree").getOrCreate()
    val data = spark.read.format("libsvm").load("data/sample_libsvm_data.txt")

    data.printSchema()
    //对标签进行类别编码
    val labelIndexer = new StringIndexer()
      .setInputCol("label")
      .setOutputCol("indexedLabel")
      .fit(data)
    labelIndexer.transform(data).show()
    labelIndexer.labels.foreach(x => print(x))
    //对特征进行类别编码
    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexfeatures")
      .setMaxCategories(4)//设定类别特征的最大个数,超过这个类别数量被视为连续的
      .fit(data)

    featureIndexer.transform(data).show()
    //切分生成训练集和测试集
    val Array(trainingData, testData) = data.randomSplit(Array(0.7, 0.3))

    //根据特征和标签创建决策树
    val dt = new org.apache.spark.ml.classification.DecisionTreeClassifier()
      .setLabelCol("indexedLabel")
      .setFeaturesCol("indexfeatures")

    //将预测结果转化为原始标签
    val labelConverter = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("predictedLabel")
      .setLabels(labelIndexer.labels)

    //建立管道
    val pipline = new Pipeline()
      .setStages(Array(labelIndexer, featureIndexer, dt, labelConverter))

    //将训练集压入管道进行模型训练
    val model = pipline.fit(trainingData)

    //对测试数据进行测试
    val predictions = model.transform(testData)

    //展示测试结果
    predictions.select("predictedLabel", "label", "features").show(5, false)

    //对预测结果的准确性评估
    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("indexedLabel")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")

    val accuracy = evaluator.evaluate(predictions)

    println("testError = " + (1.0 -accuracy))

    //拿到决策树
    val treeModel = model.stages(2).asInstanceOf[DecisionTreeClassificationModel]
    println("Learned classification tree model:\n" + treeModel.toDebugString)
  }
}
