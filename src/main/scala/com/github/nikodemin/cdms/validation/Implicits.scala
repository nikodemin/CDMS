package com.github.nikodemin.cdms.validation


object Implicits {

  //  implicit class ErrorsOps(errors: Seq[ValidationError]) {
  //    def fail: IO[Status, Nothing] = ZIO.fail(Status.INVALID_ARGUMENT.withDescription(errors.map(e => s"Path: ${e.path} Error: ${e.errorMessage}")
  //      .reduceLeft((s1, s2) => s"$s1\n$s2")))
  //  }

}
