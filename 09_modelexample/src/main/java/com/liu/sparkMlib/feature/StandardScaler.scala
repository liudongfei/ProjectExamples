package com.liu.sparkMlib.feature

import org.apache.spark.sql.SparkSession

class StandardScaler {

}

object StandardScaler {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("standerscaler").getOrCreate()
    val dataFrame = spark.read.format("libsvm").load("data/mllib/sample_libsvm_data.txt")

    val scaler = new org.apache.spark.ml.feature.StandardScaler()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")
      .setWithStd(true)
      .setWithMean(false)

    val scalerModel = scaler.fit(dataFrame)

    val scaledData = scalerModel.transform(dataFrame)
    scaledData.show()
  }
}