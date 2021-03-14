package com.github.nikodemin.cdms.dao.impl

//class OrderDaoImpl(implicit session: Session) extends OrderDao.Service {
//  def add(order: OrderAdd): Task[Long] =
//    c"""
//    CREATE (order: Order {totalCost: '${order.paymentInfo.amount}'${
//      order.paymentInfo.currency.fold("")(currency => s", currency: '$currency'")
//    }})
//    WITH order
//    ${
//      if (order.paymentInfo.paymentMethod.isCard) {
//        val creditCard = order.paymentInfo.creditCard.get
//        s"""
//        MERGE (creditCard: CreditCard {cardHolder: '${creditCard.cardHolder}', cvv: ${creditCard.cvv},
//         number: '${creditCard.number}',dueDate: date(${creditCard.dueDate})})
//        MERGE (order) -[:CC_PAYED]-> (creditCard)
//        """
//      } else ""
//    }
//    OPTIONAL MATCH (customer: Customer) WHERE ID(customer) = ${order.customerId}
//    MERGE (customer) -[:ORDERED]-> (order)
//    WITH order
//
//    OPTIONAL MATCH pattern=(product) WHERE ID(product) IN ${order.productIds.mkString("[", ",", "]")}
//    FOREACH (p IN nodes(pattern) | MERGE (order) -[:HAS]-> (p))
//    WITH order
//
//    ${
//      if (order.delivery.isPostalDelivery) {
//        val postalDelivery = order.delivery.postalDelivery.get
//        s"""
//        MERGE (postalDelivery: PostalDelivery {zipCode: '${postalDelivery.zipCode}'
//        ${postalDelivery.country.fold("")(country => s", country: '$country'")}
//        ${postalDelivery.city.fold("")(city => s", city: '$city'")}})
//        MERGE (order) -[:DELIVERED_BY_POST]-> (postalDelivery)
//        """
//      } else if (order.delivery.isPickUpDelivery) {
//        val pickUpDelivery = order.delivery.pickUpDelivery.get
//        s"""
//        OPTIONAL MATCH (pickUpPoint: PickUpPoint) WHERE ID(pickUpPoint)=${pickUpDelivery.pickUpPointId}
//        MERGE (order) -[:DELIVERED_BY_PICK_UP_IN]-> (pickUpPoint)
//        """
//      } else {
//        val courierDelivery = order.delivery.courierDelivery.get
//        val addressInfo = courierDelivery.addressInfo
//        s"""
//        MERGE (addressInfo: AddressInfo {country: '${addressInfo.country}', city: '${addressInfo.city}',
//          street: '${addressInfo.street}', homeNumber: ${addressInfo.homeNumber}, flatNumber: ${addressInfo.flatNumber}
//          ${addressInfo.homeAddition.fold("")(homeAddition => s", homeAddition: $homeAddition")}})
//        MERGE (order) -[:DELIVERED_BY_COURIER_TO]-> (addressInfo)
//        """
//      }
//    }
//    RETURN ID(order)
//    """.single().get(0).asLong()
//}
