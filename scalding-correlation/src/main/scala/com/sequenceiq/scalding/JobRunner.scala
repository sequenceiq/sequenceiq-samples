package com.sequenceiq.scalding

import org.apache.hadoop
import com.twitter.scalding.Tool


/**
 * Entrypoint for Hadoop to kick off the job.
 *
 * Borrowed from com.twitter.scalding.Tool
 */
object JobRunner {
  def main(args : Array[String]) {
    hadoop.util.ToolRunner.run(new hadoop.conf.Configuration, new Tool, args)
  }
}