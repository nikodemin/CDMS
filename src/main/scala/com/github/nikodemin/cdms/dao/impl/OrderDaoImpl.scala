package com.github.nikodemin.cdms.dao.impl

import com.github.nikodemin.cdms.api.Types.OrderAdd
import com.github.nikodemin.cdms.dao.impl.Util._
import com.github.nikodemin.cdms.dao.{Edges, OrderDao, Vertices}
import gremlin.scala.ScalaGraph
import zio.Task

class OrderDaoImpl(implicit graph: ScalaGraph) extends OrderDao.Service {
  private val g = graph.traversal

  def add(orderAdd: OrderAdd): Task[Long] = exec {
    val orderVertex = graph + (Vertices.order.toString,
      Vertices.order.totalCost -> orderAdd.paymentInfo.amount,
      Vertices.order.currency -> orderAdd.paymentInfo.currency)

    val paymentInfo = orderAdd.paymentInfo

    val addPaymentInfo = () => graph + (Vertices.paymentInfo.toString,
      Vertices.paymentInfo.amount -> paymentInfo.amount,
      Vertices.paymentInfo.currency -> paymentInfo.currency,
      Vertices.paymentInfo.paymentType -> paymentInfo.`type`
    )

    val paymentInfoVertex = paymentInfo.creditCard.fold(addPaymentInfo()) { cc =>
      val result = addPaymentInfo()

      g.V.has(Vertices.creditCard.toString, Vertices.creditCard.number, cc.number)
        .fold()
        .coalesce(_.unfold.addE(Edges.hasCreditCard.toString).from(result), _.addV(Vertices.creditCard.toString)
          .property(Vertices.creditCard.number -> cc.number)
          .property(Vertices.creditCard.cardHolder -> cc.cardHolder)
          .property(Vertices.creditCard.cvv -> cc.cvv)
          .property(Vertices.creditCard.dueDate -> cc.dueDate)
          .from(result)
        )

      result
    }

    orderVertex
  }.flatMap(getElemId)
}
