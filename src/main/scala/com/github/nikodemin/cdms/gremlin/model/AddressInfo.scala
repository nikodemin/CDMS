package com.github.nikodemin.cdms.gremlin.model

import gremlin.scala.id

case class AddressInfo(
                        country: String,
                        city: String,
                        street: String,
                        homeNumber: Int,
                        homeNumberAddition: Option[String],
                        flatNumber: Int,
                        @id id: Option[Long]
                      )
