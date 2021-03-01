package com.github.nikodemin.cdms.dao

import com.github.nikodemin.cdms.dao.impl.OrderDaoImpl
import com.github.nikodemin.cdms.proto.cdms.{OrderAdd, PaymentInfo, PickUpDelivery}
import org.neo4j.driver._
import org.scalatest.wordspec.AnyWordSpecLike

class OrderDaoSpec extends AnyWordSpecLike {
  val driver: Driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "123456"),
    Config.builder().withLogging(Logging.slf4j()).build())
  implicit val session: Session = driver.session
  val orderDao = new OrderDaoImpl

  "OrderDao" should {
    "create order" in {
      val order = OrderAdd(PaymentInfo(amount = 100, paymentMethod = PaymentInfo.PaymentMethod.CASH),
        OrderAdd.Delivery.PickUpDelivery(PickUpDelivery("4")), "5")
      orderDao.add(order)
    }
  }
}