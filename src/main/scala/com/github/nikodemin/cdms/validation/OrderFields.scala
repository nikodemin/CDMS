package com.github.nikodemin.cdms.validation

import com.github.nikodemin.cdms.proto.cdms._
import com.github.nikodemin.validation.{Fields, FieldsOption, Validators}

object OrderFields {

  object orderFields extends Fields[OrderAdd] {
    val customerId: Validators.Field[OrderAdd, String] = field(_.customerId)
    val productIds: Validators.Field[OrderAdd, Seq[String]] = field(_.productIds)
    val delivery: Validators.Field[OrderAdd, OrderAdd.Delivery] = field(_.delivery)
    val paymentInfo: Validators.Field[OrderAdd, PaymentInfo] = field(_.paymentInfo)
  }

  object deliveryFields extends Fields[OrderAdd.Delivery] {
    val courierDelivery: Validators.Field[OrderAdd.Delivery, Option[CourierDelivery]] = field(_.courierDelivery)
    val pickUpDelivery: Validators.Field[OrderAdd.Delivery, Option[PickUpDelivery]] = field(_.pickUpDelivery)
    val postalDelivery: Validators.Field[OrderAdd.Delivery, Option[PostalDelivery]] = field(_.postalDelivery)
  }

  object courierDeliveryFields extends FieldsOption[CourierDelivery] {
    val addressInfo: Validators.Field[Option[CourierDelivery], Option[AddressInfoAdd]] = field(_.addressInfo)
  }

  object addressInfoFields extends FieldsOption[AddressInfoAdd] {
    val country: Validators.Field[Option[AddressInfoAdd], Option[String]] = field(_.country)
    val city: Validators.Field[Option[AddressInfoAdd], Option[String]] = field(_.city)
    val street: Validators.Field[Option[AddressInfoAdd], Option[String]] = field(_.street)
    val homeNumber: Validators.Field[Option[AddressInfoAdd], Option[Int]] = field(_.homeNumber)
    val homeAddition: Validators.Field[Option[AddressInfoAdd], Option[String]] = field(_.homeAddition).to(_.flatten)
    val flatNumber: Validators.Field[Option[AddressInfoAdd], Option[Int]] = field(_.flatNumber)
  }

  object pickUpDeliveryFields extends FieldsOption[PickUpDelivery] {
    val pickUpPointId: Validators.Field[Option[PickUpDelivery], Option[String]] = field(_.pickUpPointId)
  }

  object postalDeliveryFields extends FieldsOption[PostalDelivery] {
    val zipCode: Validators.Field[Option[PostalDelivery], Option[String]] = field(_.zipCode)
    val country: Validators.Field[Option[PostalDelivery], Option[String]] = field(_.country).to(_.flatten)
    val city: Validators.Field[Option[PostalDelivery], Option[String]] = field(_.city).to(_.flatten)
  }

  object paymentInfoFields extends Fields[PaymentInfo] {
    val amount: Validators.Field[PaymentInfo, Long] = field(_.amount)
    val currency: Validators.Field[PaymentInfo, Option[String]] = field(_.currency)
    val paymentMethod: Validators.Field[PaymentInfo, PaymentInfo.PaymentMethod] = field(_.paymentMethod)
    val creditCard: Validators.Field[PaymentInfo, Option[CreditCard]] = field(_.creditCard)
  }

  object creditCardFields extends FieldsOption[CreditCard] {
    val cvv: Validators.Field[Option[CreditCard], Option[Int]] = field(_.cvv)
    val cardHolder: Validators.Field[Option[CreditCard], Option[String]] = field(_.cardHolder)
    val number: Validators.Field[Option[CreditCard], Option[String]] = field(_.number)
    val dueDate: Validators.Field[Option[CreditCard], Option[String]] = field(_.dueDate)
  }

}
