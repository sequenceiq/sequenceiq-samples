package com.sequenceiq.samples.scalding

import com.twitter.scalding.{FieldConversions, Source, Args, Csv}
import cascading.tuple.Fields

/**
 *
 */
trait Output extends FieldConversions {
  /**
   * Implement this for new output type
   */
  def output(args: Args): Source
}

trait CsvOutput extends Output {

  override def output(args: Args): Source = {
    Csv(outputFile(args), outputSeparator(args), Fields.ALL, skipHeader(args), writeHeader(args), quote(args))
  }

  private def outputFile(args: Args): String = {
    if (args.boolean("output")) args("output")
    else throw new IllegalArgumentException("output argument is missing")
  }

  private def outputSeparator(args: Args): String = {
    if (args.boolean("outputSeparator")) args("outputSeparator")
    else ","
  }

  private def skipHeader(args: Args): Boolean = {
    if (args.boolean("outputSkipHeader")) {
      args("outputSkipHeader") match {
        case "true" => true
        case "false" => false
        case _ => false
      }
    }
    else false
  }

  private def writeHeader(args: Args): Boolean = {
    if (args.boolean("outputWriteHeader")) {
      args("outputWriteHeader") match {
        case "true" => true
        case "false" => false
        case _ => false
      }
    }
    else false
  }

  private def quote(args: Args): String = {
    if (args.boolean("outputQuote")) args("outputQuote")
    else "\""
  }
}


