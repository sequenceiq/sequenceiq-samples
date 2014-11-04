package com.sequenceiq.spark

import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.rdd.RDD

object TopKMain {
  import org.apache.spark.SparkContext._
  def main(args: Array[String]) {
    val input = args(0)
    val top = args(1)
    val storageLevel = args.length match {
      case 3 => args(2)
      case _ => "none"
    }

    val conf = new SparkConf()
    val sc = new SparkContext(conf)

    val txtFile = doPersist(
      sc.textFile(input).map(line => line.split(" ")), storageLevel)


    val result = txtFile
      .map(arr => (arr(0), 1))
      .reduceByKey(_ + _)
      .map(pair => pair.swap)
      .top(top.toInt)

    result.foreach{case (number, value) => println (value + " : " + number)}



  }

  def doPersist(rdd: RDD[Array[String]], level: String): RDD[Array[String]] = {
    level match {
      case "cache" => rdd.persist(StorageLevel.MEMORY_ONLY)
      case "mem_and_disk" => rdd.persist(StorageLevel.MEMORY_AND_DISK)
      case "mem_ser" => rdd.persist(StorageLevel.MEMORY_ONLY_SER)
      case "mem_and_disk_ser" => rdd.persist(StorageLevel.MEMORY_AND_DISK_SER)
      case _ => rdd
    }
  }

}
