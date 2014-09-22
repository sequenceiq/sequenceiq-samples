package com.sequenceiq.spark

import org.apache.spark.{SparkContext, SparkConf}
import com.sequenceiq.spark.job.{CustomCorrelationJob, StatsCorrelationJob}

object Main {

  def main(args: Array[String]) {
    if (args.length < 1) {
      println("usage: <input> ")
      System.exit(0)
    }

    val input = args(0)
    val conf = new SparkConf().setAppName("Spark Correlation Example")
    val context = new SparkContext(conf)

    val textInput = context.textFile(input)

    val customResult = new CustomCorrelationJob().computeCorrelation(textInput)
    val statResult = new StatsCorrelationJob().computeCorrelation(textInput)

    println(customResult)
    println(statResult)

    context.stop()
  }

}
