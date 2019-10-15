package com.liu.sparkMlib.model.classification

import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.classification.BinaryLogisticRegressionSummary
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.max

/**
  * 逻辑回归
  *
  * 1、训练模型
  *
  * 2、获取训练模型的summary
  *     roc
  *     F-score
  *     threshold
  * 3、测试模型
  *
  * 4、评估模型
  *
  */
class LogisticRegression {

}

object LogisticRegression {


  /**
    * 二分类逻辑回归
    */
  def binaryLogicRegression(): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)

    val spark = SparkSession.builder().master("local").appName("Logistic example").getOrCreate()
    val data = spark.read.format("libsvm").load("data/sample_libsvm_data.txt")
    println("数据的前10行：")
    data.show(10)
    val Array(train, test) = data.randomSplit(Array(0.7, 0.3))
    import spark.implicits._

    //创建逻辑回归模型
    val lr = new org.apache.spark.ml.classification.LogisticRegression()
      .setMaxIter(10)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)

    //训练模型
    val lrModel = lr.fit(train)

    //输出系数和截距
    println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")


    val trainingSummary = lrModel.summary

    val objectHistory = trainingSummary.objectiveHistory
    println("objectHistory:")
    objectHistory.foreach(loss => println(loss))//损失函数

    val binarySummary = trainingSummary.asInstanceOf[BinaryLogisticRegressionSummary]

    //计算roc曲线
    val roc = binarySummary.roc
    println("计算roc曲线：")
    roc.show()
    println(s"areaUnderROC: ${binarySummary.areaUnderROC}")


    //获取各个样本的的F-score
    val fMeasure = binarySummary.fMeasureByThreshold
    fMeasure.show(10)

    //获取最大F-score
    val maxFMeasure = fMeasure.select(max("F-Measure")).head().getDouble(0)
    val bestThreshold = fMeasure.where($"F-Measure" === maxFMeasure)
      .select("threshold").head().getDouble(0)
    println("选择最好分类阈值:" + bestThreshold)
    lrModel.setThreshold(bestThreshold)

    val prediction = lrModel.transform(test)
    prediction.show(10)

    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val accuracy = evaluator.evaluate(prediction)
    println("testError = : \t" + (1 - accuracy))


  }

  /**
    * 多分类逻辑回归
    */
  def multinomialLogicRegression():Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)

    val spark = SparkSession.builder().master("local").appName("Logistic example").getOrCreate()
    val training = spark.read.format("libsvm").load("data/sample_libsvm_data.txt")

    // 设置ElasticNet混合参数,范围为[0，1]。
    // 对于α= 0，惩罚是L2惩罚。 对于alpha = 1，它是一个L1惩罚。 对于0 <α<1，惩罚是L1和L2的组合。 默认值为0.0，这是一个L2惩罚。

    val mlr = new org.apache.spark.ml.classification.LogisticRegression()
      .setMaxIter(10)//迭代次数
      .setRegParam(0.3)//正则化参数
      .setElasticNetParam(0.8)//
      .setFamily("multinomial")
    val mlrModel = mlr.fit(training)
    //输出逻辑回归的系数和截距
    println(s"Coefficients: ${mlrModel.coefficientMatrix} Intercept: ${mlrModel.interceptVector}")
    //TODO 没有summary

  }
  def main(args: Array[String]): Unit = {
    binaryLogicRegression()
//    multinomialLogicRegression()
  }

}
