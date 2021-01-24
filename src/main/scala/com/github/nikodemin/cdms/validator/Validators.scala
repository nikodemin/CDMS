package com.github.nikodemin.cdms.validator

import java.time.LocalDate
import java.util.UUID

import cats.data.Validated
import com.github.nikodemin.cdms.proto._
import monocle.Lens
import monocle.macros.GenLens

import scala.util.Try

object Validators {
  type AllErrorsOr[A] = Validated[Error, A]

  val currencies = Set("RUB", "EUR", "USD")

  case class Error(path: String, errorMessage: String)

  case class Field[S, F](name: String, lens: Lens[S, F]) {
    def >=>[F2](field: Field[F, F2]) = Field(s"$name.${field.name}", lens.composeLens(field.lens))

    def validate(source: S, validators: Seq[F => Option[String]]): Seq[Error] = {
      val field = lens.get(source)
      validators.map(_ (field))
        .filter(_.isDefined)
        .map(errStr => Error(name, errStr.get))
    }
  }

  object Fields {
    def empty[S, A]: A => S => S = _ => f => f

    object FromRoot {
      val customerId: Field[OrderAdd, String] = Field("orderAdd.customerId", GenLens[OrderAdd](_.customerId))
      val productIds: Field[OrderAdd, Seq[String]] = Field("orderAdd.productIds", GenLens[OrderAdd](_.productIds))
      val delivery: Field[OrderAdd, OrderAdd.Delivery] = Field("orderAdd.delivery", GenLens[OrderAdd](_.delivery))
    }

    val courierDelivery: Field[OrderAdd.Delivery, Option[CourierDelivery]] = Field("courierDelivery", Lens[OrderAdd.Delivery, Option[CourierDelivery]]
      (_.courierDelivery)(empty))
    val pickUpDelivery: Field[OrderAdd.Delivery, Option[PickUpDelivery]] = Field("pickUpDelivery", Lens[OrderAdd.Delivery, Option[PickUpDelivery]]
      (_.pickUpDelivery)(empty))
    val postalDelivery: Field[OrderAdd.Delivery, Option[PostalDelivery]] = Field("postalDelivery", Lens[OrderAdd.Delivery, Option[PostalDelivery]]
      (_.postalDelivery)(empty))

    val courierDeliveryToAddressInfo: Field[Option[CourierDelivery], Option[AddressInfoAdd]] =
      Field("addressInfo", Lens[Option[CourierDelivery], Option[AddressInfoAdd]](_.map(_.addressInfo))(empty))

    val addressInfoToCountry: Field[Option[AddressInfoAdd], Option[String]] =
      Field("country", Lens[Option[AddressInfoAdd], Option[String]](_.map(_.country))(empty))
    val addressInfoToCity: Field[Option[AddressInfoAdd], Option[String]] =
      Field("city", Lens[Option[AddressInfoAdd], Option[String]](_.map(_.city))(empty))
    val addressInfoToStreet: Field[Option[AddressInfoAdd], Option[String]] =
      Field("street", Lens[Option[AddressInfoAdd], Option[String]](_.map(_.street))(empty))
    val addressInfoToHomeNumber: Field[Option[AddressInfoAdd], Option[Int]] =
      Field("homeNumber", Lens[Option[AddressInfoAdd], Option[Int]](_.map(_.homeNumber))(empty))
    val addressInfoToHomeNumberAddition: Field[Option[AddressInfoAdd], Option[String]] =
      Field("homeNumberAddition", Lens[Option[AddressInfoAdd], Option[String]](_.flatMap(_.homeAddition))(empty))
    val addressInfoToFlatNumber: Field[Option[AddressInfoAdd], Option[Int]] =
      Field("flatNumber", Lens[Option[AddressInfoAdd], Option[Int]](_.map(_.flatNumber))(empty))

    val pickUpDeliveryToPickUpPointId: Field[Option[PickUpDelivery], Option[String]] =
      Field("pickUpPointId", Lens[Option[PickUpDelivery], Option[String]](_.map(_.pickUpPointId))(empty))
    val postalDeliveryToZipCode: Field[Option[PostalDelivery], Option[String]] =
      Field("zipCode", Lens[Option[PostalDelivery], Option[String]](_.map(_.zipCode))(empty))
    val postalDeliveryToCountry: Field[Option[PostalDelivery], Option[String]] =
      Field("country", Lens[Option[PostalDelivery], Option[String]](_.flatMap(_.country))(empty))
    val postalDeliveryToCity: Field[Option[PostalDelivery], Option[String]] =
      Field("city", Lens[Option[PostalDelivery], Option[String]](_.flatMap(_.city))(empty))

    object Implicits {

      implicit class StringValidations[S](field: Field[S, Option[String]]) {
        def validateRequiredAndLength(length: Int)(implicit source: S): Seq[Error] = field.validate(source, Seq(
          f => f.map(f2 => if (f2.length > length) Some(s"${field.name} is too long") else None).getOrElse(Some(s"${field.name} is empty"))
        ))

        def validateLength(length: Int)(implicit source: S): Seq[Error] = field.validate(source, Seq(
          f => f.flatMap(f2 => if (f2.length > length) Some(s"${field.name} is too long") else None)
        ))

        def validateRequiredAndUUID(implicit source: S): Seq[Error] = field.validate(source, Seq(
          f => f.map(f2 => Try(UUID.fromString(f2)).toEither.fold(_ => Some(s"${field.name} cannot be parsed as UUID"), _ => None))
            .getOrElse(Some(s"${field.name} is empty"))
        ))
      }

      implicit class Validations[S, D](field: Field[S, Option[D]]) {
        def validateRequired(implicit source: S): Seq[Error] = field.validate(source, Seq(
          f => if (f.isEmpty) Some(s"${field.name} is requred") else None
        ))
      }

    }

  }

  def validateOrder(implicit orderAdd: OrderAdd): Seq[Error] = {
    import Fields.Implicits._

    def validateCustomerId: Seq[Error] = Try(UUID.fromString(orderAdd.customerId)).toEither
      .fold(_ => Seq(Error("orderAdd.customerId", "Failed to parse UUID")), _ => Seq())

    def validateProductIds: Seq[Error] = {
      val ids = orderAdd.productIds
      if (ids.isEmpty) Seq(Error("orderAdd.productIds", "Product list is empty")) else
        ids.map(id => Try(UUID.fromString(id)).toOption)
          .zipWithIndex
          .filter(_._1.isEmpty)
          .map(e => Error(s"orderAdd.productIds[${e._2}]", "Failed to parse UUID"))
    }

    def validatePaymentInfo: Seq[Error] = {

      val paymentInfo = orderAdd.paymentInfo

      def validateAmount: Seq[Error] = if (paymentInfo.amount > 0) Seq() else Seq(Error("orderAdd.paymentInfo.amount", "Negative amount"))

      def validateCurrency: Seq[Error] = paymentInfo.currency.map {
        currency => if (currencies.contains(currency)) Seq() else Seq(Error("orderAdd.paymentInfo.currency", s"Unsuppoorted currency value: $currency"))
      }.getOrElse(Seq())

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

    def validateDelivery = if (orderAdd.delivery.isCourierDelivery) {
      val courierDeliveryField = Fields.FromRoot.delivery >=> Fields.courierDelivery
      val addressInfoField = courierDeliveryField >=> Fields.courierDeliveryToAddressInfo
      val countryField = addressInfoField >=> Fields.addressInfoToCountry
      val cityField = addressInfoField >=> Fields.addressInfoToCity
      val streetField = addressInfoField >=> Fields.addressInfoToStreet
      val homeNumberField = addressInfoField >=> Fields.addressInfoToHomeNumber
      val homeNumberAdditionField = addressInfoField >=> Fields.addressInfoToHomeNumberAddition
      val flatNumberField = addressInfoField >=> Fields.addressInfoToFlatNumber

      val errors = courierDeliveryField.validateRequired
      if (errors.isEmpty) {
        val errors2 = addressInfoField.validateRequired
        if (errors2.isEmpty) {
          countryField.validateRequiredAndLength(100) ++
            cityField.validateRequiredAndLength(100) ++
            streetField.validateRequiredAndLength(100) ++
            homeNumberField.validateRequired ++
            homeNumberAdditionField.validateLength(5) ++
            flatNumberField.validateRequired
        } else errors2
      } else errors

    } else if (orderAdd.delivery.isPickUpDelivery) {
      (Fields.FromRoot.delivery >=> Fields.pickUpDelivery >=> Fields.pickUpDeliveryToPickUpPointId).validateRequiredAndUUID
    } else if (orderAdd.delivery.isPostalDelivery) {
      val postalDeliveryField = Fields.FromRoot.delivery >=> Fields.postalDelivery
      val zipCodeField = postalDeliveryField >=> Fields.postalDeliveryToZipCode
      val countryField = postalDeliveryField >=> Fields.postalDeliveryToCountry
      val cityField = postalDeliveryField >=> Fields.postalDeliveryToCity

      val errors = postalDeliveryField.validateRequired

      if (errors.isEmpty) {
        zipCodeField.validateRequiredAndLength(20) ++
          countryField.validateLength(100) ++
          cityField.validateLength(100)
      } else errors
    } else {
      Seq(Error("orderAdd.delivery", "Delivery is not set"))
    }

    validateDelivery ++ validateCustomerId ++ validateProductIds ++ validatePaymentInfo
  }
}
