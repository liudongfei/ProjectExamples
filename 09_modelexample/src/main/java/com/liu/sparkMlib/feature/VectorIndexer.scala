package com.liu.sparkMlib.feature

import org.apache.spark.sql.SparkSession

class VectorIndexer {

}

object VectorIndexer {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("vector").getOrCreate()
    val data = spark.read.format("libsvm").load("data/mllib/sample_libsvm_data.txt")

    val indexer = new org.apache.spark.ml.feature.VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexed")
      .setMaxCategories(10)

    val indexerModel = indexer.fit(data)

    val categoricalFeatures: Set[Int] = indexerModel.categoryMaps.keys.toSet
    println(s"Chose ${categoricalFeatures.size} categorical features: " +
      categoricalFeatures.mkString(", "))

    // Create new column "indexed" with categorical values transformed to indices
    val indexedData = indexerModel.transform(data)
    indexedData.show()
  }
}