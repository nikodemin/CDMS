package com.github.nikodemin.cdms.mapping

import com.github.nikodemin.cdms.api.Types.Person
import com.github.nikodemin.cdms.gremlin.model.{Person => Entity}

trait PersonMapper {
  def entityToPerson(src: Entity): Person
}

object PersonMapper {
  def live(addressInfoMapper: AddressInfoMapper, imageMapper: ImageMapper): PersonMapper = new PersonMapper {

    import io.scalaland.chimney.dsl._

    override def entityToPerson(src: Entity): Person = src match {
      case e: Entity.StoreKeeper => e.into[Person.StoreKeeper].withFieldComputed(_.id, _.id.get).transform
      case e: Entity.Customer => e.into[Person.Customer].withFieldComputed(_.id, _.id.get)
        .withFieldComputed(_.addresses, _.addresses.map(addressInfoMapper.entityToAddressInfo))
        .withFieldComputed(_.avatar, _.avatar.map(imageMapper.entityToImage))
        .transform
      case e: Entity.Courier => e.into[Person.Courier].withFieldComputed(_.id, _.id.get).transform
      case e: Entity.Admin => e.into[Person.Admin].withFieldComputed(_.id, _.id.get).transform
      case e: Entity.Manager => e.into[Person.Manager].withFieldComputed(_.id, _.id.get).transform
    }
  }
}
