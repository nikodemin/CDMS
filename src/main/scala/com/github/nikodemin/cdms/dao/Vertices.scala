package com.github.nikodemin.cdms.dao

import java.time.OffsetDateTime
import java.util.UUID

import com.github.nikodemin.cdms.api.Types.{Currency, PaymentType}
import gremlin.scala.Key

object Vertices {

  object order {
    override def toString: String = "Order"

    val id: Key[UUID] = Key("id")
    val totalCost: Key[Long] = Key("totalCost")
    val currency: Key[Currency] = Key("currency")
  }

  object creditCard {
    override def toString: String = "CreditCard"

    val number: Key[String] = Key("number")
    val cardHolder: Key[String] = Key("cardHolder")
    val dueDate: Key[OffsetDateTime] = Key("dueDate")
    val cvv: Key[Int] = Key("cvv")
  }

  object paymentInfo {
    override def toString: String = "paymentInfo"

    val paymentType: Key[PaymentType] = Key("paymentType")
    val amount: Key[Long] = Key("amount")
    val currency: Key[Currency] = Key("currency")
  }

}
