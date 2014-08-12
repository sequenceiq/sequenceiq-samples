package com.sequenceiq.spark

import org.apache.hadoop.conf.Configuration
import org.apache.spark.deploy.yarn.{ClientArguments, Client}
import org.apache.spark.SparkConf
import scala.xml.XML

object SparkRunner {
  /*
  java -jar spark-submit-runner-1.0.jar \
  --jar spark-submit-app-1.0.jar \
  --class com.sequenceiq.spark.Main \
  --driver-memory 1g \
  --executor-memory 1g \
  --executor-cores 1 \
  --arg hdfs://sandbox:9000/input/sample.txt \
  --arg /output \
  --arg 10 \
  --arg 10
   */
  def main(args: Array[String]) {
    val config = new Configuration()
    fillProperties(config, getPropXmlAsMap("config/core-site.xml"))
    fillProperties(config, getPropXmlAsMap("config/yarn-site.xml"))

    System.setProperty("SPARK_YARN_MODE", "true")

    val sparkConf = new SparkConf()
    val cArgs = new ClientArguments(args, sparkConf)

    new Client(cArgs, config, sparkConf).run()

  }

  def getPropXmlAsMap(filePath: String): Map[String, String] = {
    Map[String, String]() ++ (for (yarnProp <- (XML.loadFile(filePath) \\ "property"))
    yield (yarnProp \ "name").text -> (yarnProp \ "value").text).toMap
  }

  def fillProperties(config: Configuration, props: Map[String, String]) {
    for ((key, value) <- props) config.set(key, value)
  }
}
