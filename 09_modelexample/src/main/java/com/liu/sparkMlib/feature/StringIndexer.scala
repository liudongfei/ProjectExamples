package com.liu.sparkMlib.feature

import org.apache.spark.sql.SparkSession

class StringIndexer {

}

object StringIndexer {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("stringindexer").getOrCreate()
    val df = spark.createDataFrame(
      Seq((0, "a"), (1, "b"), (2, "c"), (3, "a"), (4, "a"), (5, "c"))
    ).toDF("id", "category")

    val indexer = new org.apache.spark.ml.feature.StringIndexer()
      .setInputCol("category")
      .setOutputCol("categoryIndex")

    val indexed = indexer.fit(df).transform(df)
    indexed.show()
  }
}
