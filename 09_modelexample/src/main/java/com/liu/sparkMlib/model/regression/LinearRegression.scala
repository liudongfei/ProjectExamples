package com.liu.sparkMlib.model.regression

import org.apache.spark.sql.SparkSession

/**
  * 线性回归
  *
  */
class LinearRegression {

}

object LinearRegression {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("linear regression").getOrCreate()
    val data = spark.read.format("libsvm").load("data/mllib/sample_linear_regression_data.txt")
    //创建线性回归
    val lr = new org.apache.spark.ml.regression.LinearRegression()
      .setMaxIter(10)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)

    val lrModel = lr.fit(data)
    println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")

    val trainingSummary = lrModel.summary
    println(s"numIterations: ${trainingSummary.totalIterations}")
    println(s"objectiveHistory: [${trainingSummary.objectiveHistory.mkString(",")}]")

    trainingSummary.residuals.show()
    println(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
    println(s"r2: ${trainingSummary.r2}")
  }
}