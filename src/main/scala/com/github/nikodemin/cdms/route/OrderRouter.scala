package com.github.nikodemin.cdms.route

import akka.http.scaladsl.server.Route
import com.github.nikodemin.cdms.model.dto.OrderDto
import com.github.nikodemin.cdms.route.util.BaseRouter
import com.github.nikodemin.cdms.route.util.Implicits._
import com.github.nikodemin.cdms.service.OrderService
import sttp.model.StatusCode
import sttp.tapir.json.circe._
import sttp.tapir.server.akkahttp._
import sttp.tapir.{Endpoint, _}

import scala.concurrent.ExecutionContext

class OrderRouter(orderService: OrderService)
                 (implicit executionContext: ExecutionContext) extends BaseRouter {
  override lazy val route: Route = createOrderRoute

  override lazy val endpoints: List[Endpoint[_, _, _, _]] = List(createOrder)

  private val orderEndpoint = endpoint
    .tag("Order")
    .in("order")

  private val createOrder = orderEndpoint
    .post
    .in(jsonBody[OrderDto])
    .out(statusCode(StatusCode.Ok))
    .errorOut(statusCode(StatusCode.BadRequest))

  private val createOrderRoute = createOrder.toRoute(orderService.acceptOrder(_).map(res => if (res) Left(()) else Right(())))
}
