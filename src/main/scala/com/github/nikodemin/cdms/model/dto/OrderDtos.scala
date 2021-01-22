package com.github.nikodemin.cdms.model.dto

import java.time.LocalDate
import java.util.UUID

case class OrderDto(paymentInfo: PaymentInfoDto, delivery: DeliveryDto, customerInfo: PersonInfoDto, productIds: Seq[UUID])


sealed trait DeliveryDto

case class PostalDeliveryDto(country: Option[String], city: Option[String], zipCode: String) extends DeliveryDto

case class CourierDeliveryDto(addressInfo: AddressInfoDto) extends DeliveryDto

case class PickupDeliveryDto(pickupPointId: UUID) extends DeliveryDto


case class PersonInfoDto(firstName: String, lastName: String, email: Option[String], phoneNumber: String)

case class AddressInfoDto(country: String, city: String, street: String, homeNumber: Int, homeAddition: Option[String], flatNumber: Int)

case class PaymentInfoDto(amount: Long, currency: Option[String], paymentMethod: PaymentMethodDto)


sealed trait PaymentMethodDto

object CashDto extends PaymentMethodDto

case class CreditCardDto(number: String, cardHolder: String, dueDate: LocalDate, cvv: Int) extends PaymentMethodDto