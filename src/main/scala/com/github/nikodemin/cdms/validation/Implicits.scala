package com.github.nikodemin.cdms.validation

import com.github.nikodemin.validation.ValidationError
import io.grpc.Status
import zio.{IO, ZIO}


object Implicits {

  implicit class ErrorsOps(errors: Seq[ValidationError]) {
    def fail: IO[Status, Nothing] = ZIO.fail(Status.INVALID_ARGUMENT.withDescription(errors.map(e => s"Path: ${e.path} Error: ${e.errorMessage}")
      .reduceLeft((s1, s2) => s"$s1\n$s2")))
  }

}
