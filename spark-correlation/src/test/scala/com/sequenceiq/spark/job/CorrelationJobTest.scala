package com.sequenceiq.spark.job

import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class CorrelationJobTest extends SparkJobSpec {

  "Spark Correlation implementations" should {
    val input = Seq("1,2,9,5", "2,7,5,6","4,5,3,4","6,7,5,6")
    val correctOutput = Array(
      (0, 1, 0.620299),
      (0, 2, -0.627215),
      (0, 3, 0.11776),
      (1, 2, -0.70069),
      (1, 3, 0.552532),
      (2, 3, 0.207514)
      )

    "case 1 : return with correct output (custom spark correlation)" in {
      val inputRDD = sc.parallelize(input)
      val customCorr = new CustomCorrelationJob().computeCorrelation(inputRDD)
      customCorr must_== correctOutput
    }
    "case 2: return with correct output (stats spark correlation)" in {
      val inputRDD = sc.parallelize(input)
      val statCorr = new StatsCorrelationJob().computeCorrelation(inputRDD)
      statCorr must_== correctOutput
    }

    "case 3: equal with each other" in {
      val inputRDD = sc.parallelize(input)
      val statCorr = new StatsCorrelationJob().computeCorrelation(inputRDD)
      val customCorr = new CustomCorrelationJob().computeCorrelation(inputRDD)
      statCorr must_== customCorr
    }


  }

}
