package com.sequenceiq.spark.job

import org.apache.spark.rdd.RDD

class CustomCorrelationJob extends CorrelationJob {

  override def computeCorrelation(input: RDD[String]) : Array[(Int, Int, Double)] = {
    val numbersInput = input
      .map(line => line.split(",").map(_.toDouble))
      .cache()

    val combinedFields = (0 to numbersInput.first().size - 1).combinations(2)
    val size = numbersInput.count()
    val res = for (field <- combinedFields) yield {
      val col1Index = field.head
      val col2Index = field.last
      val tempData = numbersInput.map{arr => {
        val data1 = arr(col1Index)
        val data2 = arr(col2Index)
        (data1, data2, data1 * data2, math.pow(data1, 2), math.pow(data2, 2))
      }}
      val (sum1: Double, sum2: Double, dotProduct: Double, sq1: Double, sq2: Double) = tempData.reduce {
        case ((a1, a2, aDot, a1sq, a2sq), (b1, b2, bDot, b1sq, b2sq)) =>
          (a1 + b1, a2 + b2, aDot + bDot, a1sq + b1sq, a2sq + b2sq)
      }
      val corr = pearsonCorr(size, sum1, sum2, sq1, sq2, dotProduct)
      (col1Index, col2Index, d2d(corr))
    }
    res.toArray
  }

  def pearsonCorr(size: Long, sum1: Double, sum2: Double, sq1: Double, sq2: Double, dotProduct: Double): Double = {
    val numerator = (size * dotProduct) - (sum1 * sum2)
    val denominator = scala.math.sqrt(size * sq1 - sum1 * sum1) * scala.math.sqrt(size * sq2 - sum2 * sum2)
    numerator / denominator
  }
}
