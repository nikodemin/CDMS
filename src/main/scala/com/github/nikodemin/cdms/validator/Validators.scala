package com.github.nikodemin.cdms.validator

import java.time.LocalDate
import java.util.UUID

import cats.data.Validated
import com.github.nikodemin.cdms.proto.OrderAdd

import scala.util.Try

object Validators {
  type AllErrorsOr[A] = Validated[Error, A]

  val currencies = Set("RUB", "EUR", "USD")

  case class Error(path: String, errorMessage: String)


  def validateOrder(orderAdd: OrderAdd): Seq[Error] = {

    def validateCustomerId: Seq[Error] = Try(UUID.fromString(orderAdd.customerId)).toEither
      .fold(_ => Seq(Error("orderAdd.customerId", "Failed to parse UUID")), _ => Seq())

    def validateProductIds: Seq[Error] = orderAdd.productIds.map(id => Try(UUID.fromString(id)).toOption)
      .zipWithIndex
      .filter(_._1.isEmpty)
      .map(e => Error(s"orderAdd.productIds[${e._2}]", "Failed to parse UUID"))

    def validatePaymentInfo: Seq[Error] = {

      val paymentInfo = orderAdd.paymentInfo

      def validateAmount: Seq[Error] = if (paymentInfo.amount > 0) Seq() else Seq(Error("orderAdd.paymentInfo.amount", "Negative amount"))

      def validateCurrency: Seq[Error] = paymentInfo.currency.fold(Seq()) {
        currency => if (currencies.contains(currency)) Seq() else Seq(Error("orderAdd.paymentInfo.currency", s"Unsuppoorted currency value: $currency"))
      }

      def validateCreditCard: Seq[Error] = paymentInfo.creditCard.fold(Seq(Error("orderAdd.paymentInfo.creditCard", "Credit card is empty"))) { cc =>
        Seq(
          if (cc.cvv.toString.length == 3) None else Some(Error("orderAdd.paymentInfo.creditCard.cvv", "Cvv number should have length 3")),
          Try(LocalDate.parse(cc.dueDate)).fold(_ => Some(Error("orderAdd.paymentInfo.creditCard.dueDate", "Cannot parse date")), _ => None),
          if (cc.cardHolder.isBlank) Some(Error("orderAdd.paymentInfo.creditCard.cardHolder", "Card holder should not be empty")) else None,
          if (cc.number.isBlank) Some(Error("orderAdd.paymentInfo.creditCard.number", "Card numner should not be empty")) else None
        ).filter(_.isDefined).map(_.get)
      }

      (if (paymentInfo.paymentMethod.isCard) validateCreditCard else Seq()) ++ validateAmount ++ validateCurrency
    }

    validateCustomerId ++ validateProductIds ++ validatePaymentInfo
  }
}
