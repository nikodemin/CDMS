package com.github.nikodemin.cdms.mapping

import com.github.nikodemin.cdms.api.Types.AddressInfo
import com.github.nikodemin.cdms.gremlin.model.{AddressInfo => Entity}

trait AddressInfoMapper {
  def entityToAddressInfo(src: Entity): AddressInfo
}

object AddressInfoMapper {
  val live = new AddressInfoMapper {

    import io.scalaland.chimney.dsl._

    override def entityToAddressInfo(src: Entity): AddressInfo = src.into[AddressInfo]
      .withFieldComputed(_.id, _.id.get)
      .transform
  }
}
