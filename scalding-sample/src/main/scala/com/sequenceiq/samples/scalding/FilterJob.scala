package com.sequenceiq.samples.scalding

import com.twitter.scalding.{Source, Job, Csv, Args}
import com.sequenceiq.samples.scalding._
import cascading.tuple.Fields

/**
 *  Generic filtering job
 *  <br>------------------------------------------------<br>
 *  MandatoOperations.scalry arguments: --type int --field id --operator eq (--operand 1 || --operandList 1,2,3)<br>
 */
abstract class FilterJob(args: Args) extends Job(args) with Input with Output {
  val STRING_TYPE = "string"
  val DATE_TYPE = "date"
  val INT_TYPE = "int"
  val DOUBLE_TYPE = "double"

  val dataType = args("type")
  val filterableField = args("field")
  val operator = args("operator")

  val operand = args.getOrElse("operand", null)
  val operandList = args.getOrElse("operandList", null)

  val datePattern = args.getOrElse("date_pattern", null)
  val regex = args.getOrElse("regex", null)
  val from = args.getOrElse("from", null)
  val to = args.getOrElse("to", null)

  lazy val dateOperand = DataConverter.convertDataToDateTime(operand, datePattern);
  lazy val intOperand = DataConverter.convertDataToInt(operand);
  lazy val doubleOperand = DataConverter.convertDataToDouble(operand);

  lazy val stringListOperand = DataConverter.convertDataToStringList(operandList)
  lazy val dateListOperand = DataConverter.convertDataToDateTimeList(operandList, datePattern)
  lazy val intListOperand =  DataConverter.convertDataToIntList(operandList)
  lazy val doubleListOperand =  DataConverter.convertDataToDoubleList(operandList)

  lazy val fromDateOperand = DataConverter.convertDataToDateTime(from, datePattern)
  lazy val toDateOperand = DataConverter.convertDataToDateTime(to, datePattern)
  lazy val fromIntOperand = DataConverter.convertDataToInt(from)
  lazy val toIntOperand = DataConverter.convertDataToInt(to)
  lazy val fromDoubleOperand = DataConverter.convertDataToDouble(from)
  lazy val toDoubleOperand = DataConverter.convertDataToDouble(to)

  validation()
  input(args)
    .filter(filterableField) {field: String => createFilterCriterion(field)}
    .write(output(args))

  private def createFilterCriterion(field: String): Boolean = {
    if (regex != null) {
      field.matches(regex)
    }
    else if (operandList != null) {
      createListFilter(field)
    }
    else if (from != null && to != null) {
      createIntervalFilter(field)
    }
    else {
      createSimpleFilter(field)
    }
  }

  private def createSimpleFilter(field: String): Boolean = {
    dataType match {
      case STRING_TYPE => StringOperations(field, operator, operand)
      case DATE_TYPE => DateOperations(DataConverter.convertDataToDateTime(field, datePattern), operator, dateOperand)
      case INT_TYPE => IntOperations(DataConverter.convertDataToInt(field), operator, intOperand)
      case DOUBLE_TYPE => DoubleOperations(DataConverter.convertDataToDouble(field), operator, doubleOperand)
      case _ => false
    }
  }

  private def createListFilter(field: String): Boolean = {
    dataType match {
      case STRING_TYPE => StringOperations(field, operator, stringListOperand)
      case DATE_TYPE => DateOperations(DataConverter.convertDataToDateTime(field, datePattern), operator, dateListOperand)
      case INT_TYPE => IntOperations(DataConverter.convertDataToInt(field), operator, intListOperand)
      case DOUBLE_TYPE => DoubleOperations(DataConverter.convertDataToDouble(field), operator, doubleListOperand)
      case _ => false
    }
  }

  private def createIntervalFilter(field: String): Boolean = {
    dataType match {
      case STRING_TYPE => StringOperations(field, operator, from, to)
      case DATE_TYPE => DateOperations(DataConverter.convertDataToDateTime(field, datePattern), operator, fromDateOperand, toDateOperand)
      case INT_TYPE => IntOperations(DataConverter.convertDataToInt(field), operator,fromIntOperand, toIntOperand)
      case DOUBLE_TYPE => DoubleOperations(DataConverter.convertDataToDouble(field), operator, fromDoubleOperand, toDoubleOperand)
      case _ => false
    }
  }

  def validation() {
     if (operand == null && operandList == null){
       throw new IllegalArgumentException("Please specify an operand or operandList parameter")
     }
  }
}

/**
 * Usage Example: <br>
 * yarn jar scalding-sample-0.1.jar com.sequenceiq.samples.scalding.CsvToCsvFilterJob --local --schema id,name --input /workspace/data/input.csv --type int --operator eq --field id --operand 1 --output /workspace/data/output.csv
 */
class CsvToCsvFilterJob(args: Args) extends FilterJob(args) with CsvInput with CsvOutput
