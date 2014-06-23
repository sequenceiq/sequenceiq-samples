package com.sequenceiq.scalding.hbase

import com.twitter.scalding.{FieldConversions, Tsv, Job, Args}
import scala.compat.Platform
import parallelai.spyglass.hbase.{HBasePipeConversions, HBaseSource}
import cascading.tuple.Fields

class HBaseWriterJob(args: Args) extends Job(args) with HBasePipeConversions with FieldConversions{
  val tableName = args("tableName")
  val quorum_name = args("quorum")
  val quorum_port = args("quorumPort").toInt

  val scheme = List('key, 'correlation)
  val familyNames = List("corrCf")

  Tsv(args("input")).read
    .toBytesWritable(scheme)
    .write(
      new HBaseSource(
        tableName,
        quorum_name + ":" + quorum_port,
        scheme.head,
        familyNames,
        scheme.tail.map((x: Symbol) => new Fields(x.name)).toList,
        timestamp = Platform.currentTime
      ))
}
