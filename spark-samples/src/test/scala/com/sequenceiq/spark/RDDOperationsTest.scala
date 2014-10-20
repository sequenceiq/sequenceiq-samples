package com.sequenceiq.spark

import org.apache.spark.{SparkContext, SparkConf}
import org.testng.Assert
import org.testng.annotations.{Test, AfterClass, BeforeClass}

class RDDOperationsTest {
  import org.apache.spark.SparkContext._

  @transient var sc: SparkContext = _
  var conf = new SparkConf(false)

  @BeforeClass
  def setUp() {
    sc = new SparkContext("local", "test", conf)
  }

  @AfterClass
  def tearDown() {
    sc.stop()
    sc = null
  }

  @Test
  def testMap() {
    val input = Seq(1 ,2 ,3, 4)
    val expectedOutput = Array("s1", "s2", "s3", "s4")

    val output = sc.makeRDD(input).map(num => "s" + num.toString())

    Assert.assertEquals(output.collect(), expectedOutput)

  }

  @Test
  def testFlatMap() {
    val input = Seq(1 ,2 ,3, 4)
    val expectedOutput = Array("s1", "t1", "s2", "t2", "s3", "t3", "s4", "t4")

    val output = sc.makeRDD(input).flatMap(num => List("s" + num.toString(), "t" + num.toString()))

    Assert.assertEquals(output.collect(), expectedOutput)

  }

  @Test
  def testFilter() {
    val input = Seq(1 ,2 ,3, 4)
    val expectedOutput = Array(1,2)

    val output = sc.makeRDD(input).filter(num => num < 3)

    Assert.assertEquals(output.collect(), expectedOutput)

  }

  @Test
  def testCount() {
    val input = Seq(1 ,2 ,3, 4)
    val expectedOutput = 4

    val output = sc.makeRDD(input).count()

    Assert.assertEquals(output, expectedOutput)

  }

  @Test
  def testCountByValue() {
    val input = Seq(1 ,1, 2, 2, 2 ,3, 4)
    val expectedOutput = Map(1 -> 2, 2 -> 3, 3 -> 1, 4 -> 1)

    val output = sc.makeRDD(input).countByValue()

    Assert.assertEquals(output, expectedOutput)

  }

  @Test
  def testDistinct() {
    val input = Seq(1, 1, 2)
    val expectedOutput = Array(1, 2)

    val output = sc.makeRDD(input).distinct()

    Assert.assertEquals(output.collect(), expectedOutput)

  }

  @Test
  def testReduce() {
    val input = Seq(1 ,2 ,3, 4)
    val expectedOutput = 10

    val output = sc.makeRDD(input).reduce((n,m) => n + m)

    Assert.assertEquals(output, expectedOutput)
  }

  @Test
  def testGroupBy() {
    val input = Seq((1, 1), (1, 2), (1,3), (2, 4))
    val expectedOutput = Array((1, 1), (1, 2), (1,3)) // Groups: ((1, 1), (1, 2), (1,3)) and ((2, 4))

    val output = sc.makeRDD(input).groupBy(x => x._1) // group by first elements of the tuples

    Assert.assertEquals(output.collect.apply(0)._2.toArray, expectedOutput)
  }

  @Test
  def testFirst() {
    val input = Seq(1 ,2 ,3, 4)
    val expectedOutput = 1

    val output = sc.makeRDD(input).first()

    Assert.assertEquals(output, expectedOutput)

  }

  @Test
  def testTake() {
    val input = Seq(1 ,2 ,3, 4)
    val expectedOutput = Array(1,2)

    val output = sc.makeRDD(input).take(2)

    Assert.assertEquals(output, expectedOutput)

  }

  @Test
  def testTakeOrdered() {
    val input = Seq(4, 1, 2, 3)
    val expectedOutput = Array(1,2)

    val output = sc.makeRDD(input).takeOrdered(2)

    Assert.assertEquals(output, expectedOutput)

  }

  @Test
  def testCartesian() {
    val input = Seq(1 ,2 ,3)
    val expectedOutput = Array((1,4),(1,5),(2,4),(2,5),(3,4),(3,5))

    val otherInput = sc.makeRDD(Seq(4, 5))
    val output = sc.makeRDD(input).cartesian(otherInput)

    Assert.assertEquals(output.collect(), expectedOutput)

  }

  @Test
  def testSortBy() {
    val input = Seq(4 ,2 ,1, 3)
    val expectedOutput = Array(1, 2, 3, 4)

    val output = sc.makeRDD(input).sortBy(x => x, true)

    Assert.assertEquals(output.collect(), expectedOutput)
  }

  @Test
  def testZip() {
    val input = Seq(1, 2, 3)
    val otherInput = Seq("1", "2", "3")
    val expectedOutput = Array((1, "1"), (2, "2"), (3, "3"))

    val output = sc.makeRDD(input).zip(sc.makeRDD(otherInput))

    Assert.assertEquals(output.collect(), expectedOutput)
  }

  @Test
  def testZipWithIndex() {
    val input = Seq(2, 3, 4)
    val expectedOutput = Array((2, 0), (3, 1), (4, 2))

    val output = sc.makeRDD(input).zipWithIndex()

    Assert.assertEquals(output.collect(), expectedOutput)
  }

  @Test
  def testUnion() {
    val input1 = Seq(1, 2)
    val input2 = Seq(3, 4)
    val expectedOutput = Array(1, 2, 3, 4)

    val output = sc.makeRDD(input1).union(sc.makeRDD(input2))

    Assert.assertEquals(output.collect(), expectedOutput)
  }

  @Test
  def testIntersection() {
    val input1 = Seq(1, 2)
    val input2 = Seq(2, 3)
    val expectedOutput = Array(2)

    val output = sc.makeRDD(input1).intersection(sc.makeRDD(input2))

    Assert.assertEquals(output.collect(), expectedOutput)

  }

  @Test
  def testSubtract() {
    val input1 = Seq(1, 2, 3, 4)
    val input2 = Seq(3, 4)
    val expectedOutput = Array(1, 2)

    val output = sc.makeRDD(input1).subtract(sc.makeRDD(input2))

    Assert.assertEquals(output.collect(), expectedOutput)

  }

  @Test
  def testPartitionWithGlom() {
    val input = Seq(1 ,2 ,3, 4)
    val expectedOutput = Array(Array(1,2), Array(3,4))

    val output = sc.makeRDD(input, 2)

    Assert.assertEquals(output.glom().collect(), expectedOutput)

  }

  @Test
  def testCoalesce() {
    val input = Seq(1 ,2 ,3, 4)
    val expectedOutput = Array(Array(1, 2, 3, 4))

    val output = sc.makeRDD(input, 2).coalesce(1)

    Assert.assertEquals(output.glom().collect(), expectedOutput)

  }

  @Test
  def testMapPartitions() {
    val input = Seq(1 ,2 ,3, 4)
    val expectedOutput = Array(Array(2,4), Array(6,8))

    val output = sc.makeRDD(input, 2).mapPartitions(itr => itr.map(num => num * 2))

    Assert.assertEquals(output.glom().collect(), expectedOutput)

  }

  @Test
  def testMapPartitionsWithIndex() {
    val input = Seq(1 ,2 ,3, 4)
    val expectedOutput = Array(Array((0, 1), (0,2)), Array((1, 3), (1,4)))

    val output = sc.makeRDD(input, 2).mapPartitionsWithIndex((idx, itr) => itr.map(s => (idx, s)))

    Assert.assertEquals(output.glom().collect(), expectedOutput)

  }

  /// Pair RDD functions

  @Test
  def testLookup() {
    val input = Seq((1, 2), (2, 4), (3, 3))
    val expectedOutput = Seq(2)

    val output = sc.makeRDD(input).lookup(1)

    Assert.assertEquals(output, expectedOutput)
  }

  @Test
  def testSortByKey() {
    val input = Seq((4, 2, 1), (2, 5, 1), (1, 4, 1), (3, 0, 1))
    val expectedOutput = Array((1, (4, 1)), (2, (5, 1)), (3, (0, 1)), (4, (2, 1)))

    val output = sc.makeRDD(input).map{case (x, y, z) => (x, (y, z))}.sortByKey()

    Assert.assertEquals(output.collect(), expectedOutput)
  }

  @Test
  def testReduceByKey() {
    val input = Seq((1, 2), (1, 3), (2, 4))
    val expectedOutput = Array((1, 5), (2, 4))

    val output = sc.makeRDD(input).reduceByKey(_ + _)

    Assert.assertEquals(output.collect(), expectedOutput)
  }

  @Test
  def testJoin() {
    val input1 = sc.makeRDD(Seq((1, 4), (2, 5)))
    val input2 = sc.makeRDD(Seq((1, '1'), (2, '2')))
    val expectedOutput = Array((1, (4, '1')), (2, (5, '2')))

    val output = input1.join(input2)

    Assert.assertEquals(output.collect(), expectedOutput)
  }

}
