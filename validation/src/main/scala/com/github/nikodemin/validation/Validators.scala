package com.github.nikodemin.validation

import monocle.Lens

object Validators {

  case class Conditional(errors: Seq[Error]) {
    def ifPassed(newErrors: Seq[Error]): Conditional = if (errors.isEmpty) Conditional(newErrors) else this
  }

  case class FieldGen[S, F]()

  case class Field[S, F](name: String, lens: Lens[S, F]) {
    def >=>[F2](field: Field[F, F2]): Field[S, F2] = Field(s"$name.${field.name}", lens.composeLens(field.lens))

    def validate(validators: (F => Option[String])*)(implicit source: S): Seq[Error] = {
      val field = lens.get(source)
      validators.map(_ (field))
        .filter(_.isDefined)
        .map(errStr => Error(name, errStr.get))
    }

    def validateSeq(validators: (F => Seq[String])*)(implicit source: S): Seq[Error] = {
      val field = lens.get(source)
      validators.flatMap(_ (field))
        .map(errStr => Error(name, errStr))
    }
  }

  def empty[S, A]: A => S => S = _ => f => f
}
