package com.liu.sparkMlib.model.clutering

import org.apache.spark.ml.clustering.GaussianMixture
import org.apache.spark.sql.SparkSession

/**
  * 高斯混合模型
  */
class GMModel {

}

object GMModel {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("GMM").getOrCreate()

    val dataset = spark.read.format("libsvm").load("data/mllib/sample_kmeans_data.txt")

    val gmm = new GaussianMixture()
      .setK(2)

    val model = gmm.fit(dataset)
    for (i <- 0 until model.getK) {
      println(s"Gaussian $i:\nweight=${model.weights(i)}\n" +
        s"mu=${model.gaussians(i).mean}\nsigma=\n${model.gaussians(i).cov}\n")
    }
  }
}