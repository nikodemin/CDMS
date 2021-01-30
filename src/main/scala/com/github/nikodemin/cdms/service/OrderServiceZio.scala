package com.github.nikodemin.cdms.service

import com.github.nikodemin.cdms.proto.cdms.ZioCdms.OrderService
import com.github.nikodemin.cdms.proto.cdms.{GenericResponse, OrderAdd, OrderStateChange}
import io.grpc.Status
import zio.IO

class OrderServiceZio extends OrderService {
  override def add(request: OrderAdd): IO[Status, GenericResponse] = ???

  override def changeState(request: OrderStateChange): IO[Status, GenericResponse] = ???
}
