package com.sequenceiq.scalding.correlation

trait CorrelationOp {
  def calculateCorrelation(size: Long, su1: Double, su2: Double, sq1: Double, sq2: Double, dotProd: Double) : Double = {
    val dividend = (size * dotProd) - (su1 * su2)
    val divisor = scala.math.sqrt(size * sq1 - su1 * su1) * scala.math.sqrt(size * sq2 - su2 * su2)
    dividend / divisor
  }
}
