package com.sequenceiq.samples.scalding

import com.twitter.scalding.{FieldConversions, Csv, Source, Args}
import scala.collection.mutable.ListBuffer

/**
 * */
trait Input extends FieldConversions {
  /**
   * Implement this for handling new input type
   */
  def input(args: Args): Source
}

/**
 * Trait for reading CSV inputs (local & hdfs)
 * <br>------------------------------------------------<br>
 * Mandatory arguments: --schema "s,c,h,e,m,a" --input /path/to/file<br>
 * Optional arguments: --skipHeader false --writeHeader false --separator , --quote \"<br>
 *
 * Usage: call readCsv function to get the csv file from local/hdfs
 */
trait CsvInput extends Input{

  /**
   * Read CSV from local/hdfs path
   */
  def input(args: Args): Source = {
    Csv(inputFile(args), separator(args), schema(args), skipHeader(args), writeHeader(args), quote(args))
  }
  // temporary solution
  private def schema(args: Args): List[Symbol] = {
    val schema = ListBuffer[Symbol]()
    if (args.boolean("schema")) {
      for (elem <- args("schema").split(separator(args))) schema += Symbol(elem)
      return schema.toList
    }
    /*   // Another solution
    TextLine(args("schema"))
        .map('line -> 'config) { line: String => createColumns(line, separator()) }
        .discard('line) // clear fields
      schema.toList

      def createColumns(s: String, sep: String): Iterable[String] = {
        for (elem <- s.split(sep)) schema += Symbol(elem)
        List(s) // return
      }
    }*/
    else throw new IllegalArgumentException("schema argument is missing")
  }

  private def inputFile(args: Args): String = {
    if (args.boolean("input")) args("input")
    else throw new IllegalArgumentException("input argument is missing")
  }

  private def skipHeader(args: Args): Boolean = {
    if (args.boolean("skipHeader")) {
      args("skipHeader") match {
        case "true" => true
        case "false" => false
        case _ => false
      }
    }
    else false
  }

  private def writeHeader(args: Args): Boolean = {
    if (args.boolean("writeHeader")) {
      args("writeHeader") match {
        case "true" => true
        case "false" => false
        case _ => false
      }
    }
    else false
  }

  private def separator(args: Args): String = {
    if (args.boolean("separator")) args("separator")
    else ","
  }

  private def quote(args: Args): String = {
    if (args.boolean("quote")) args("quote")
    else "\""
  }

}
