package com.github.nikodemin.cdms.service

import com.github.nikodemin.cdms.dao.impl.OrderDaoImpl
import com.github.nikodemin.cdms.proto.cdms.ZioCdms.ZOrderService
import com.github.nikodemin.cdms.proto.cdms.{GenericResponse, OrderAdd, OrderStateChange}
import com.github.nikodemin.cdms.validation.Implicits._
import com.github.nikodemin.cdms.validation.ValidationService
import io.grpc.Status
import zio.IO

class OrderService(orderDao: OrderDaoImpl) extends ZOrderService[Any, Any] {
  override def add(request: OrderAdd): IO[Status, GenericResponse] = {
    val errors = ValidationService.validateOrder(request)
    if (errors.nonEmpty) errors.fail else {
      orderDao.add(request)
      ???
    }
  }

  override def changeState(request: OrderStateChange): IO[Status, GenericResponse] = ???
}
