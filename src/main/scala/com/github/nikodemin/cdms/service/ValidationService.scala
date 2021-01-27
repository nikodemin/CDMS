package com.github.nikodemin.cdms.service

import java.time.LocalDate

import com.github.nikodemin.cdms.proto._
import com.github.nikodemin.validation.Error
import com.github.nikodemin.validation.Implicits._
import com.github.nikodemin.validation.Validators._
import monocle.Lens
import monocle.macros.GenLens

object ValidationService {
  val currencies = Set("RUB", "EUR", "USD")

  object OrderFields {

    object FromRoot {
      val customerId: Field[OrderAdd, String] = Field("orderAdd.customerId", GenLens[OrderAdd](_.customerId))
      val productIds: Field[OrderAdd, Seq[String]] = Field("orderAdd.productIds", GenLens[OrderAdd](_.productIds))
      val delivery: Field[OrderAdd, OrderAdd.Delivery] = Field("orderAdd.delivery", GenLens[OrderAdd](_.delivery))
      val paymentInfo: Field[OrderAdd, PaymentInfo] = Field("orderAdd.paymentInfo", GenLens[OrderAdd](_.paymentInfo))
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

    val paymentInfoToAmount: Field[PaymentInfo, Long] = Field("amount", GenLens[PaymentInfo](_.amount))
    val paymentInfoToCurrency: Field[PaymentInfo, Option[String]] = Field("currency", GenLens[PaymentInfo](_.currency))
    val paymentInfoToPaymentMethod: Field[PaymentInfo, PaymentInfo.PaymentMethod] = Field("paymentMethod", GenLens[PaymentInfo](_.paymentMethod))
    val paymentInfoToCreditCard: Field[PaymentInfo, Option[CreditCard]] = Field("creditCard", GenLens[PaymentInfo](_.creditCard))

    val creditCardToCvv: Field[Option[CreditCard], Option[Int]] = Field("cvv", Lens[Option[CreditCard], Option[Int]](_.map(_.cvv))(empty))
    val creditCardToCardHolder: Field[Option[CreditCard], Option[String]] = Field("cardHolder", Lens[Option[CreditCard], Option[String]](_.map(_.cardHolder))(empty))
    val creditCardToNumber: Field[Option[CreditCard], Option[String]] = Field("number", Lens[Option[CreditCard], Option[String]](_.map(_.number))(empty))
    val creditCardToDueDate: Field[Option[CreditCard], Option[String]] = Field("dueDate", Lens[Option[CreditCard], Option[String]](_.map(_.dueDate))(empty))
  }


  def validateOrder(implicit orderAdd: OrderAdd): Seq[Error] = {

    def validateCustomerId: Seq[Error] = OrderFields.FromRoot.customerId.validateUUID

    def validateProductIds: Seq[Error] = Conditional(OrderFields.FromRoot.productIds.validateNotEmpty)
      .ifPassed {
        OrderFields.FromRoot.productIds.validateUUIDElements
      }

    def validatePaymentInfo: Seq[Error] = {

      val amountField = OrderFields.FromRoot.paymentInfo >=> OrderFields.paymentInfoToAmount
      val currencyField = OrderFields.FromRoot.paymentInfo >=> OrderFields.paymentInfoToCurrency
      val paymentMethodField = OrderFields.FromRoot.paymentInfo >=> OrderFields.paymentInfoToPaymentMethod
      val creditCardField: Field[OrderAdd, Option[CreditCard]] = OrderFields.FromRoot.paymentInfo >=> OrderFields.paymentInfoToCreditCard

      val cvvField = creditCardField >=> OrderFields.creditCardToCvv
      val cardHolderField = creditCardField >=> OrderFields.creditCardToCardHolder
      val cardNumberField = creditCardField >=> OrderFields.creditCardToNumber
      val cardDueDateField = creditCardField >=> OrderFields.creditCardToDueDate

      (if (paymentMethodField.lens.get(orderAdd).isCard) {
        creditCardField.validateRequired.ifPassed {
          cvvField.validateRequired.ifPassed {
            cvvField.validateLength(3)
          } ++
            cardHolderField.validateRequired.ifPassed {
              cardHolderField.validateLength(100)
            } ++
            cardNumberField.validateRequired.ifPassed {
              cardNumberField.validateLength(100)
            } ++
            cardDueDateField.validateRequired.ifPassed {
              cardDueDateField.validateOnParseError(LocalDate.parse, "LocalDate")
            }
        }.errors
      } else Seq()) ++
        amountField.validatePositive ++
        currencyField.validateOneOfOrAbsent(currencies.toSeq: _*)
    }

    def validateDelivery: Seq[Error] = if (orderAdd.delivery.isCourierDelivery) {
      val courierDeliveryField = OrderFields.FromRoot.delivery >=> OrderFields.courierDelivery
      val addressInfoField = courierDeliveryField >=> OrderFields.courierDeliveryToAddressInfo
      val countryField = addressInfoField >=> OrderFields.addressInfoToCountry
      val cityField = addressInfoField >=> OrderFields.addressInfoToCity
      val streetField = addressInfoField >=> OrderFields.addressInfoToStreet
      val homeNumberField = addressInfoField >=> OrderFields.addressInfoToHomeNumber
      val homeNumberAdditionField = addressInfoField >=> OrderFields.addressInfoToHomeNumberAddition
      val flatNumberField = addressInfoField >=> OrderFields.addressInfoToFlatNumber

      courierDeliveryField.validateRequired
        .ifPassed {
          addressInfoField.validateRequired
        }
        .ifPassed {
          countryField.validateRequired.ifPassed {
            countryField.validateLength(100)
          } ++
            cityField.validateRequired.ifPassed {
              cityField.validateLength(100)
            } ++
            streetField.validateRequired.ifPassed {
              streetField.validateLength(100)
            } ++
            homeNumberField.validateRequired ++
            homeNumberAdditionField.validateLength(5) ++
            flatNumberField.validateRequired
        }
    } else if (orderAdd.delivery.isPickUpDelivery) {
      val pickUpPointId = OrderFields.FromRoot.delivery >=> OrderFields.pickUpDelivery >=> OrderFields.pickUpDeliveryToPickUpPointId
      pickUpPointId.validateRequired.ifPassed {
        pickUpPointId.validateUUID
      }
    } else if (orderAdd.delivery.isPostalDelivery) {
      val postalDeliveryField = OrderFields.FromRoot.delivery >=> OrderFields.postalDelivery
      val zipCodeField = postalDeliveryField >=> OrderFields.postalDeliveryToZipCode
      val countryField = postalDeliveryField >=> OrderFields.postalDeliveryToCountry
      val cityField = postalDeliveryField >=> OrderFields.postalDeliveryToCity

      Conditional(postalDeliveryField.validateRequired)
        .ifPassed {
          zipCodeField.validateRequired.ifPassed {
            zipCodeField.validateLength(20)
          } ++
            countryField.validateLength(100) ++
            cityField.validateLength(100)
        }
    } else {
      Seq(Error("orderAdd.delivery", "Delivery is not set"))
    }

    validateDelivery ++ validateCustomerId ++ validateProductIds ++ validatePaymentInfo
  }
}
