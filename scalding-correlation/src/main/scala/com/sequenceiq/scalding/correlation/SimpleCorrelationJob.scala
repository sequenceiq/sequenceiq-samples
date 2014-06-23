package com.sequenceiq.scalding.correlation

import com.twitter.scalding.{Csv,Tsv, Job, Args}
import cascading.tuple.Fields

class SimpleCorrelationJob(args: Args) extends Job(args) with CorrelationOp {
  val comparableColumn1 = args("column1")
  val comparableColumn2 = args("column2")
  val samplePercent = args.getOrElse("samplePercent","1.00").toDouble

  val scheme = new Fields("id", "num1", "num2", "num3", "num4", "num5")

  Csv(args("input"), fields = scheme, skipHeader = true).read
  .sample(samplePercent)
  .map((comparableColumn1,comparableColumn2) -> ('prod, 'compSq1, 'compSq2)){
    values : (Double, Double) =>
      (values._1 * values._2, math.pow(values._1, 2), math.pow(values._2, 2))
  }
  .groupAll{
    _.size
      .sum[Double](comparableColumn1 -> 'compSum1)
      .sum[Double](comparableColumn2 -> 'compSum2)
      .sum[Double]('compSq1 -> 'normSq1)
      .sum[Double]('compSq2 -> 'normSq2)
      .sum[Double]('prod -> 'dotProduct)
  }
  .limit(1)
  .project('size,'compSum1, 'compSum2, 'normSq1, 'normSq2, 'dotProduct)
  .map(('size, 'compSum1, 'compSum2,'normSq1, 'normSq2, 'dotProduct)
    -> ('key, 'correlation)){
    fields : (Long, Double, Double, Double, Double, Double) =>
      val (size, sum1, sum2, normSq1, normSq2, dotProduct) = fields
      val corr = calculateCorrelation(size, sum1, sum2, normSq1, normSq2, dotProduct)
      (comparableColumn1 + "-" + comparableColumn2, corr)
  }
  .project('key, 'correlation)
  .write(Tsv(args("output")))

}
