package com.liu.sparkMlib.model.classification

import org.apache.spark.ml.classification.LinearSVC
import org.apache.spark.sql.SparkSession

/**
  * 线性支持向量机
  *
  */
class LinearSVM {

}

object LinearSVM {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("LinearSVM").getOrCreate()
    val training = spark.read.format("libsvm").load("data/mllib/sample_libsvm_data.txt")

    val lsvc = new LinearSVC().setMaxIter(10).setRegParam(0.1)

    val lsvcModel = lsvc.fit(training)
    //输出系数和截距
    println(s"Coefficients: ${lsvcModel.coefficients} Intercept: ${lsvcModel.intercept}")

  }
}