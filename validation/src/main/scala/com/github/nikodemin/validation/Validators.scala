package com.github.nikodemin.validation

import monocle.Lens

object Validators {

  case class Conditional(errors: Seq[ValidationError]) {
    def ifPassed(newErrors: Seq[ValidationError]): Conditional = if (errors.isEmpty) Conditional(newErrors) else this
  }

  case class Field[S, F](name: String, lens: Lens[S, F]) {
    def >=>[F2](field: Field[F, F2]): Field[S, F2] = Field(s"$name.${field.name}", lens.composeLens(field.lens))

    def validate(validators: (F => Option[String])*)(implicit source: S): Seq[ValidationError] = {
      val field = lens.get(source)
      validators.map(_ (field))
        .filter(_.isDefined)
        .map(errStr => ValidationError(name, errStr.get))
    }

    def validateSeq(validators: (F => Seq[String])*)(implicit source: S): Seq[ValidationError] = {
      val field = lens.get(source)
      validators.flatMap(_ (field))
        .map(errStr => ValidationError(name, errStr))
    }

    def to[F2](transform: F => F2): Field[S, F2] = this.copy(lens = lens.composeLens(Lens[F, F2](transform)(empty)))
  }

  def empty[S, A]: A => S => S = _ => f => f
}
