package com.github.nikodemin.cdms.service

import com.github.nikodemin.cdms.model.dto.OrderDto

import scala.concurrent.Future

class OrderService {
  def acceptOrder(order: OrderDto): Future[Boolean] = ???
}
