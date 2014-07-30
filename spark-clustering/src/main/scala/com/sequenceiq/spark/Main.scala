package com.sequenceiq.spark

import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.clustering.{KMeansModel, KMeans}
import org.apache.spark.mllib.linalg.Vectors

object Main {
  def main(args: Array[String]) {
    if (args.length < 4) {
      println("usage: <input> <output> <numClusters> <maxIterations> <runs> - optional")
      System.exit(0)
    }

    val conf = new SparkConf
    conf.setAppName("Spark KMeans Example")
    val context = new SparkContext(conf)

    val input = args(0)
    val output = args(1)
    val K = args(2).toInt
    val maxIteration = args(3).toInt
    val runs = calculateRuns(args)

    val data = context.textFile(input).map {
      line => Vectors.dense(line.split(',').map(_.toDouble))
    }.cache()

    val clusters: KMeansModel = KMeans.train(data, K, maxIteration, runs)
    println("cluster centers: " + clusters.clusterCenters.mkString(","))

    val vectorsAndClusterIdx = data.map{ point =>
      val prediction = clusters.predict(point)
      (point.toString, prediction)
    }

    vectorsAndClusterIdx.saveAsTextFile(output)

    context.stop()
  }

  def calculateRuns(args: Array[String]): Int = {
    if (args.length > 4) args(4).toInt
    else 1
  }
}
