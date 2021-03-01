package com.github.nikodemin.validation

import java.util.UUID

import com.github.nikodemin.validation.Validators.{Conditional, Field}

import scala.util.Try

object Implicits {

  implicit def conditionalToErrorSeq(conditional: Conditional): Seq[ValidationError] = conditional.errors

  implicit def errorSeqToConditional(errors: Seq[ValidationError]): Conditional = Conditional(errors)

  implicit class StringValidations[S](field: Field[S, String]) {
    def validateLength(length: Int)(implicit source: S): Seq[ValidationError] = field.validate(
      f => if (f.length > length) Some(s"${field.name} is too long") else None
    )

    def validateOnParseError(function: String => Any, formatName: String)(implicit source: S): Seq[ValidationError] = field.validate(
      value => Try(function(value)).toEither.fold(_ => Some(s"${field.name} cannot be parsed as $formatName"), _ => None)
    )

    def validateUUID(implicit source: S): Seq[ValidationError] = validateOnParseError(UUID.fromString, "UUID")
  }

  implicit class StringOptionValidations[S](field: Field[S, Option[String]]) {
    def validateLength(length: Int)(implicit source: S): Seq[ValidationError] = field.validate(
      f => f.flatMap(f2 => if (f2.length > length) Some(s"${field.name} is too long") else None)
    )

    def validateOnParseError(function: String => Any, formatName: String)(implicit source: S): Seq[ValidationError] = field.validate(
      f => f.flatMap(f2 => Try(function(f2)).toEither.fold(_ => Some(s"${field.name} cannot be parsed as $formatName"), _ => None))
    )

    def validateUUID(implicit source: S): Seq[ValidationError] = validateOnParseError(UUID.fromString, "UUID")
  }

  implicit class StringSeqValidations[S](field: Field[S, Seq[String]]) {
    def validateElementsLength(length: Int)(implicit source: S): Seq[ValidationError] = field.validateSeq(
      f => f.map(f2 => if (f2.length > length) Some(s"${field.name} is too long") else None)
        .filter(_.isDefined).map(_.get)
    )

    def validateElementsOnParseError(function: String => Any, formatName: String)(implicit source: S): Seq[ValidationError] = field.validateSeq(
      f => f.zipWithIndex.map(fi => Try(function(fi._1)).toEither.fold(_ => Some(s"${field.name}[${fi._2}] cannot be parsed as $formatName"), _ => None))
        .filter(_.isDefined).map(_.get)
    )

    def validateUUIDElements(implicit source: S): Seq[ValidationError] = validateElementsOnParseError(UUID.fromString, "UUID")
  }

  implicit class OptionValidations[S, D](field: Field[S, Option[D]]) {
    def validateRequired(implicit source: S): Seq[ValidationError] = field.validate(
      f => if (f.isEmpty) Some(s"${field.name} is requred") else None
    )

    def validateOneOfOrAbsent(values: D*)(implicit source: S): Seq[ValidationError] = field.validate(
      f => f.flatMap(v => if (!values.contains(v)) Some(s"Value $v is not one of $values") else None)
    )
  }

  implicit class SeqValidations[S, D](field: Field[S, Seq[D]]) {
    def validateNotEmpty(implicit source: S): Seq[ValidationError] = field.validate(
      f => if (f.isEmpty) Some(s"${field.name} is empty") else None
    )
  }

  implicit class NumberOptionValidations[S, D: Numeric](field: Field[S, Option[D]]) {
    def validatePositive(implicit source: S): Seq[ValidationError] = field.validate(
      f => {
        val value = f.map(implicitly[Numeric[D]].toInt)
        value.flatMap(v => if (v < 0) Some(s"Value $v is negative") else None)
      }
    )

    def validateRange(min: Int, max: Int)(implicit source: S): Seq[ValidationError] = field.validate(
      f => {
        val value = f.map(implicitly[Numeric[D]].toInt)
        value.flatMap(v => if (v > max || v < min) Some(s"Value $v is out or range [$min, $max]") else None)
      }
    )

    def validateLength(length: Int)(implicit source: S): Seq[ValidationError] = field.validate(
      f => {
        val value = f.map(implicitly[Numeric[D]].toInt)
        value.flatMap(v => if (v.toString.length != length) Some(s"Value $v should have length $length") else None)
      }
    )
  }

  implicit class NumberValidations[S, D: Numeric](field: Field[S, D]) {
    def validatePositive(implicit source: S): Seq[ValidationError] = field.validate(
      f => {
        val value = implicitly[Numeric[D]].toInt(f)
        if (value < 0) Some(s"Value $value is negative") else None
      }
    )

    def validateRange(min: Int, max: Int)(implicit source: S): Seq[ValidationError] = field.validate(
      f => {
        val value = implicitly[Numeric[D]].toInt(f)
        if (value > max || value < min) Some(s"Value $value is out or range [$min, $max]") else None
      }
    )

    def validateLength(length: Int)(implicit source: S): Seq[ValidationError] = field.validate(
      f => {
        val value = implicitly[Numeric[D]].toInt(f)
        if (value.toString.length != length) Some(s"Value $value should have length $length") else None
      }
    )
  }

  implicit class CommonValidations[S, D](field: Field[S, D]) {
    def validateOneOf(values: D*)(implicit source: S): Seq[ValidationError] = field.validate(
      f => if (!values.contains(f)) Some(s"Value $f is not one of $values") else None
    )
  }

}
