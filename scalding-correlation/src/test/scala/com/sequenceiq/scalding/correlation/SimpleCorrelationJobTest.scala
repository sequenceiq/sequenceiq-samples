package com.sequenceiq.scalding.correlation

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable.Specification
import com.twitter.scalding.{Csv,Tsv, JobTest, FieldConversions}
import cascading.tuple.Fields

@RunWith(classOf[JUnitRunner])
class SimpleCorrelationJobTest  extends Specification {
  "A SimpleCorrelation Job" should {
    val input = List((1,2,3,3,4,5),(2,1,2,3,4,5),(3,4,5,3,4,5))
    val correctOutputLimit = 0.8

    JobTest("com.sequenceiq.scalding.correlation.SimpleCorrelationJob")
      .arg("input", "fakeInput")
      .arg("output", "fakeOutput")
      .arg("column1", "num1")
      .arg("column2", "num2")
      .arg("correlationThreshold", "0.8")
      .source(Csv("fakeInput", ",", new Fields("id","num1","num2","num3","num4","num5"),skipHeader = true), input)
      .sink[(String, Double)](Tsv("fakeOutput", fields = Fields.ALL)) {
      outputBuf =>
        val actualOutput = outputBuf.toList.head._2
        "return greater correlation result than 0.8" in {
          correctOutputLimit must be_< (actualOutput)
        }
    }
      .run
      .finish
  }
}
