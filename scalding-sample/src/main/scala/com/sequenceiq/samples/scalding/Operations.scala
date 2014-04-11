package com.sequenceiq.samples.scalding

import org.joda.time.DateTime

trait Operations[T] {

  def in(field: T, operand: List[T]): Boolean = operand contains field

  def notIn(field: T, operand: List[T]): Boolean = !in(field, operand)

  def isGreater(field: T, operand: T): Boolean = field match {
    case i: Int => i > operand.asInstanceOf[Int]
    case i: Double => i > operand.asInstanceOf[Double]
    case d: DateTime => d.isAfter(operand.asInstanceOf[DateTime])
    case _ => false
  }

  def isGreaterOrEqual(field: T, operand: T): Boolean = !isLess(field, operand)

  def isEqual(field: T, operand: T): Boolean = field match {
    case i: Int => i == operand.asInstanceOf[Int]
    case i: Double => i == operand.asInstanceOf[Double]
    case d: DateTime => d.isEqual(operand.asInstanceOf[DateTime])
    case s: String => s.equals(operand.asInstanceOf[String])
    case _ => false
  }

  def isNotEqual(field: T, operand: T): Boolean = !isEqual(field, operand)

  def isLess(field: T, operand: T): Boolean = field match {
    case i: Int => i < operand.asInstanceOf[Int]
    case i: Double => i < operand.asInstanceOf[Double]
    case d: DateTime => d.isBefore(operand.asInstanceOf[DateTime])
    case _ => false
  }

  def isLessOrEqual(field: T, operand: T): Boolean = !isGreater(field, operand)

  def exclusiveInterval(field: T, from: T, to: T): Boolean = isGreater(field, from) && isLess(field, from)

  def inclusiveInterval(field: T, from: T, to: T): Boolean = isGreaterOrEqual(field, from) && isLessOrEqual(field, to)

  def apply(field: T, operator: String, operand: T): Boolean = {
    operator match {
      case "eq" => isEqual(field, operand)
      case "notEq" => isNotEqual(field, operand)
      case "gt" => isGreater(field, operand)
      case "ge" => isGreaterOrEqual(field, operand)
      case "lt" => isLess(field, operand)
      case "le" => isLessOrEqual(field, operand)
      case _ => true
    }

  }

  def apply(field: T, operator: String, operand: List[T]): Boolean = {
    operator match {
      case "in" => in(field, operand)
      case "notIn" => notIn(field, operand)
      case _ => true
    }
  }

  def apply(field: T, operator: String, from: T, to: T): Boolean = {
    operator match {
      case "inclusiveInterval" => inclusiveInterval(field, from, to)
      case "exclusiveInterval" => exclusiveInterval(field, from, to)
      case _ => true
    }
  }
}

object IntOperations extends Operations[Int]

object DoubleOperations extends Operations[Double]

object DateOperations extends Operations[DateTime]

object StringOperations extends Operations[String]

