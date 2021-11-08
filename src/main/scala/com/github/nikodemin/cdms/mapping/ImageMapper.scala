package com.github.nikodemin.cdms.mapping

import com.github.nikodemin.cdms.api.Types.{Image, ImageIn}
import com.github.nikodemin.cdms.gremlin.model.{Image => Entity}

import java.time.OffsetDateTime

trait ImageMapper {
  def imageInToEntity(src: ImageIn): Entity

  def entityToImage(src: Entity): Image
}

object ImageMapper {
  val live: ImageMapper = new ImageMapper {

    import io.scalaland.chimney.dsl._

    override def imageInToEntity(src: ImageIn): Entity = src.into[Entity]
      .withFieldComputed(_.date, _ => OffsetDateTime.now)
      .withFieldConst(_.authorId, None)
      .transform

    override def entityToImage(src: Entity): Image = src.into[Image]
      .withFieldComputed(_.id, _.id.get)
      .transform
  }
}
