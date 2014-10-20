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
    val input = Seq(1, 2, 3, 4)
    val expectedOutput = Array("s1", "s2", "s3", "s4")

    val output = sc.makeRDD(input).map(num => "s" + num.toString())

    Assert.assertEquals(output.collect(), expectedOutput)

  }

  @Test
  def testFlatMap() {
    val input = Seq(1, 2, 3, 4)
    val expectedOutput = Array("s1", "t1", "s2", "t2", "s3", "t3", "s4", "t4")

    val output = sc.makeRDD(input).flatMap(num => List("s" + num.toString(), "t" + num.toString()))

    Assert.assertEquals(output.collect(), expectedOutput)

  }

  @Test
  def testAggregate() {
    val input = Seq(1, 2, 3, 4)
    val expectedOutput = 10

    val output = sc.makeRDD(input).aggregate[Int](0)(
      (x, y) => x + y, // aggregate function
      (z, w) => z + w)

    Assert.assertEquals(output, expectedOutput)

  }

  @Test
  def testFilter() {
    val input = Seq(1, 2, 3, 4)
    val expectedOutput = Array(1,2)

    val output = sc.makeRDD(input).filter(num => num < 3)

    Assert.assertEquals(output.collect(), expectedOutput)

  }

  @Test
  def testCount() {
    val input = Seq(1, 2, 3, 4)
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
  def testMinMax() {
    val input = Seq(1, 2, 3, 4)
    val expectedMin = 1
    val expectedMax = 4

    val min = sc.makeRDD(input).min()
    val max = sc.makeRDD(input).max()

    Assert.assertEquals(min, expectedMin)
    Assert.assertEquals(max, expectedMax)
  }

  @Test
  def testSum() {
    val input = Seq(1, 2, 3)
    val expectedOutput = 6.0

    val output = sc.makeRDD(input).sum()

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
    val input = Seq(1, 2, 3, 4)
    val expectedOutput = 10

    val output = sc.makeRDD(input).reduce((n,m) => n + m)

    Assert.assertEquals(output, expectedOutput)
  }

  @Test
  def testGroupBy() {
    val input = Seq((1, 1), (1, 2), (1, 3), (2, 4))
    val expectedOutput = Array(
      (1, Iterable((1, 1), (1, 2), (1,3))),
      (2, Iterable((2, 4)))
    )

    val output = sc.makeRDD(input).groupBy(x => x._1) // group by first elements of the tuples

    Assert.assertEquals(output.collect(), expectedOutput)
  }

  @Test
  def testForeachWithAccumulator() {
    val input = Seq(1, 2, 3)
    val expectedOutput = 6

    val accumulatorValue = sc.accumulator(0, "My Accumulator")

    sc.makeRDD(input).foreach(x => accumulatorValue += x)

    Assert.assertEquals(accumulatorValue.value, expectedOutput)
  }

  @Test
  def testFirst() {
    val input = Seq(1, 2, 3, 4)
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
  def testTopN() {
    val input = Seq(4, 1, 2, 3)
    val expectedOutput = Array(4,3)

    val output = sc.makeRDD(input).top(2)

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

  @Test
  def testKeyBy() {
    val input = Seq(1, 2, 3, 4)
    val expectedOutput = Array(("1", 1), ("2", 2), ("3", 3), ("4", 4))

    val output = sc.makeRDD(input).keyBy(x => x.toString)

    Assert.assertEquals(output.collect(), expectedOutput)

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
  def testCombineByKey() {
    val input = Seq((1, 2), (2, 3), (2, 4), (1, 4))
    val expectedOutput = Array((1, "24"), (2, "34"))

    val output = sc.makeRDD(input).combineByKey[String](
      (x: Int) => x.toString(), // combiner
      (s: String, value: Int) => s + value.toString(), //merge value
      (s1: String, s2: String) => s1 + s2 // merge combiners
    )

    Assert.assertEquals(output.collect(), expectedOutput)
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
  def testFoldByKey() {
    val input = Seq((1, 2), (1, 3), (2, 4))
    val expectedOutput = Array((1,7), (2, 6))

    val output = sc.makeRDD(input).foldByKey(2)(_ + _)

    Assert.assertEquals(output.collect(), expectedOutput)
  }

  @Test
  def testGroupByKey() {
    val input = Seq((1, 1), (1, 2), (1, 3), (2, 4))
    val expectedOutput = Array(
      (1, Iterable(1, 2, 3)),
      (2, Iterable(4))
    )

    val output = sc.makeRDD(input).groupByKey()

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

  @Test
  def testRightOuterJoin() {
    val input1 = sc.makeRDD(Seq((1, 4)))
    val input2 = sc.makeRDD(Seq((1, '1'), (2, '2')))
    val expectedOutput = Array((1, (Some(4), '1')), (2, (None, '2')))

    val output = input1.rightOuterJoin(input2)

    Assert.assertEquals(output.collect(), expectedOutput)
  }

  @Test
  def testLeftOuterJoin() {
    val input1 = sc.makeRDD(Seq((1, 4), (2, 5)))
    val input2 = sc.makeRDD(Seq((1, '1')))
    val expectedOutput = Array((1, (4, Some('1'))), (2, (5, None)))

    val output = input1.leftOuterJoin(input2)

    Assert.assertEquals(output.collect(), expectedOutput)
  }

  @Test
  def testGroupWith() { // alias Co-group
    val input1 = sc.makeRDD(Seq((1, 4), (2, 5)))
    val input2 = sc.makeRDD(Seq((1, '1'), (2, '2')))
    val input3 = sc.makeRDD(Seq((1, "one"), (2, "two")))
    val expectedOutput = Array(
      (1, (Iterable(4), Iterable('1'), Iterable("one"))),
      (2, (Iterable(5), Iterable('2'), Iterable("two")))
    )

    val output = input1.groupWith[Char, String](input2, input3)

    Assert.assertEquals(output.collect(), expectedOutput)
  }

}
