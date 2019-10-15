package com.liu.sparkMlib.model.clutering

import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.sql.SparkSession

class KMeansModel {

}

object KMeansModel {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("k-means model").getOrCreate()
    val dataset = spark.read.format("libsvm").load("data/mllib/sample_kmeans_data.txt")

    dataset.show(10)
    val kmeans = new KMeans().setK(2).setSeed(1L)
    val model = kmeans.fit(dataset)
    val WSSSE = model.computeCost(dataset)
    println(s"Within Set Sum of Squared Errors = $WSSSE")
    println("Cluster Centers: ")
    model.clusterCenters.foreach(println)


  }
}