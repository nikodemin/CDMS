package com.github.nikodemin.cdms.validation

//object OrderFields {
//
//  object orderFields extends Fields[OrderAdd] {
//    val customerId: Field[OrderAdd, String] = field(_.customerId)
//    val productIds: Field[OrderAdd, Seq[String]] = field(_.productIds)
//    val delivery: Field[OrderAdd, OrderAdd.Delivery] = field(_.delivery)
//    val paymentInfo: Field[OrderAdd, PaymentInfo] = field(_.paymentInfo)
//  }
//
//  object deliveryFields extends Fields[OrderAdd.Delivery] {
//    val courierDelivery: Field[OrderAdd.Delivery, Option[CourierDelivery]] = field(_.courierDelivery)
//    val pickUpDelivery: Field[OrderAdd.Delivery, Option[PickUpDelivery]] = field(_.pickUpDelivery)
//    val postalDelivery: Field[OrderAdd.Delivery, Option[PostalDelivery]] = field(_.postalDelivery)
//  }
//
//  object courierDeliveryFields extends FieldsOption[CourierDelivery] {
//    val addressInfo: Field[Option[CourierDelivery], Option[AddressInfoAdd]] = field(_.addressInfo)
//  }
//
//  object addressInfoFields extends FieldsOption[AddressInfoAdd] {
//    val country: Field[Option[AddressInfoAdd], Option[String]] = field(_.country)
//    val city: Field[Option[AddressInfoAdd], Option[String]] = field(_.city)
//    val street: Field[Option[AddressInfoAdd], Option[String]] = field(_.street)
//    val homeNumber: Field[Option[AddressInfoAdd], Option[Int]] = field(_.homeNumber)
//    val homeAddition: Field[Option[AddressInfoAdd], Option[String]] = field(_.homeAddition).to(_.flatten)
//    val flatNumber: Field[Option[AddressInfoAdd], Option[Int]] = field(_.flatNumber)
//  }
//
//  object pickUpDeliveryFields extends FieldsOption[PickUpDelivery] {
//    val pickUpPointId: Field[Option[PickUpDelivery], Option[String]] = field(_.pickUpPointId)
//  }
//
//  object postalDeliveryFields extends FieldsOption[PostalDelivery] {
//    val zipCode: Field[Option[PostalDelivery], Option[String]] = field(_.zipCode)
//    val country: Field[Option[PostalDelivery], Option[String]] = field(_.country).to(_.flatten)
//    val city: Field[Option[PostalDelivery], Option[String]] = field(_.city).to(_.flatten)
//  }
//
//  object paymentInfoFields extends Fields[PaymentInfo] {
//    val amount: Field[PaymentInfo, Long] = field(_.amount)
//    val currency: Field[PaymentInfo, Option[String]] = field(_.currency)
//    val paymentMethod: Field[PaymentInfo, PaymentInfo.PaymentMethod] = field(_.paymentMethod)
//    val creditCard: Field[PaymentInfo, Option[CreditCard]] = field(_.creditCard)
//  }
//
//  object creditCardFields extends FieldsOption[CreditCard] {
//    val cvv: Field[Option[CreditCard], Option[Int]] = field(_.cvv)
//    val cardHolder: Field[Option[CreditCard], Option[String]] = field(_.cardHolder)
//    val number: Field[Option[CreditCard], Option[String]] = field(_.number)
//    val dueDate: Field[Option[CreditCard], Option[String]] = field(_.dueDate)
//  }
//}
