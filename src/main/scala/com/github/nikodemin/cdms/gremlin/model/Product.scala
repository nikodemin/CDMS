package com.github.nikodemin.cdms.gremlin.model

import gremlin.scala.id

case class Product(
                    name: String,
                    quantity: Int,
                    description: Option[String],
                    price: Long,
                    images: List[Image],
                    @id id: Option[Long] = None
                  )
