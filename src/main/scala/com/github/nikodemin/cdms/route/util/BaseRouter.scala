package com.github.nikodemin.cdms.route.util

import akka.http.scaladsl.server.Route
import sttp.tapir.Endpoint

trait BaseRouter {
  def route: Route

  def endpoints: List[Endpoint[_, _, _, _]]
}
