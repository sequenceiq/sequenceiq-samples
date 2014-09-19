package com.sequenceiq.spark.job

import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.{Matrix, Vectors}
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.rdd.RDD

class StatsCorrelationJob extends CorrelationJob {

  override def computeCorrelation(input: RDD[String], context: SparkContext) : Array[(Int, Int, Double)] = {
    val vectors = input
      .map(line => Vectors.dense(line.split(",").map(_.toDouble)))
      .cache()

    val corr: Matrix = Statistics.corr(vectors, "pearson")
    val num = corr.numRows
    val res = for ((x, i) <- corr.toArray.zipWithIndex if (i / num) < i % num )
    yield ((i / num), (i % num), d2d(x))

    res
  }

}
