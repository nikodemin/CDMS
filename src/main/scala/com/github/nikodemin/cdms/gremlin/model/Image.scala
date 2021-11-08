package com.github.nikodemin.cdms.gremlin.model

import gremlin.scala.id

import java.time.OffsetDateTime

case class Image(
                  date: OffsetDateTime,
                  name: Option[String],
                  path: String,
                  extension: Option[String],
                  authorId: Option[Long],
                  @id id: Option[Long] = None
                )
