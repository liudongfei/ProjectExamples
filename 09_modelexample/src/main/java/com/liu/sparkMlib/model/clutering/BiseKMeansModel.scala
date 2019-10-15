package com.liu.sparkMlib.model.clutering

import org.apache.spark.ml.clustering.BisectingKMeans
import org.apache.spark.sql.SparkSession

/**
  * 二分类k-means
  */
class BiseKMeansModel {

}

object BiseKMeansModel {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("BiseKMeans").getOrCreate()
    val dataset = spark.read.format("libsvm").load("data/mllib/sample_kmeans_data.txt")
    val bkm = new BisectingKMeans().setK(2).setSeed(1)
    val model = bkm.fit(dataset)
    val cost = model.computeCost(dataset)
    println(s"Within Set Sum of Squared Errors = $cost")
    println("Cluster Centers: ")
    val centers = model.clusterCenters
    centers.foreach(println)
  }
}