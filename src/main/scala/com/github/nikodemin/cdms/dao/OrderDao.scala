package com.github.nikodemin.cdms.dao

import com.github.nikodemin.cdms.api.Types.OrderAdd
import zio.{Has, Task}

object OrderDao {
  type OrderDao = Has[Service]

  trait Service {
    def add(order: OrderAdd): Task[Long]
  }

  //    val live: URLayer[Has[Session], OrderDao] = ZLayer.fromFunction(session => new OrderDaoImpl()(session.get))
}
