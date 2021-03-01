package com.github.nikodemin.cdms.dao

import com.github.nikodemin.cdms.dao.impl.OrderDaoImpl
import com.github.nikodemin.cdms.proto.cdms.OrderAdd
import org.neo4j.driver.Session
import zio.{Has, Task, URLayer, ZLayer}

object OrderDao {
  type OrderDao = Has[Service]

  trait Service {
    def add(order: OrderAdd): Task[Long]
  }

  val live: URLayer[Has[Session], OrderDao] = ZLayer.fromFunction(session => new OrderDaoImpl()(session.get))
}
