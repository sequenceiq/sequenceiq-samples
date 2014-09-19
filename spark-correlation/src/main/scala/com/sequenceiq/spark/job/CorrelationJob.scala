package com.sequenceiq.spark.job

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

abstract class CorrelationJob {

  def computeCorrelation(input: RDD[String], context: SparkContext) : Array[(Int, Int, Double)]

  def d2d(d: Double) : Double = new java.text.DecimalFormat("#.######").format(d).toDouble

}
