package com.github.nikodemin.cdms.validation

import java.time.LocalDate

import com.github.nikodemin.cdms.proto.cdms._
import com.github.nikodemin.cdms.validation.OrderFields._
import com.github.nikodemin.validation.Error
import com.github.nikodemin.validation.Implicits._
import com.github.nikodemin.validation.Validators.{Conditional, Field}

object ValidationService {
  val currencies = Set("RUB", "EUR", "USD")

  def validateOrder(implicit orderAdd: OrderAdd): Seq[Error] = {

    def validateCustomerId: Seq[Error] = orderFields.customerId.validateUUID

    def validateProductIds: Seq[Error] = Conditional(orderFields.productIds.validateNotEmpty)
      .ifPassed {
        orderFields.productIds.validateUUIDElements
      }

    def validatePaymentInfo: Seq[Error] = {

      val amountField = orderFields.paymentInfo >=> paymentInfoFields.amount
      val currencyField = orderFields.paymentInfo >=> paymentInfoFields.currency
      val paymentMethodField = orderFields.paymentInfo >=> paymentInfoFields.paymentMethod
      val creditCardField: Field[OrderAdd, Option[CreditCard]] = orderFields.paymentInfo >=> paymentInfoFields.creditCard

      val cvvField = creditCardField >=> creditCardFields.cvv
      val cardHolderField = creditCardField >=> creditCardFields.cardHolder
      val cardNumberField = creditCardField >=> creditCardFields.number
      val cardDueDateField = creditCardField >=> creditCardFields.dueDate

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
      val courierDeliveryField = orderFields.delivery >=> deliveryFields.courierDelivery
      val addressInfoField = courierDeliveryField >=> courierDeliveryFields.addressInfo
      val countryField = addressInfoField >=> addressInfoFields.country
      val cityField = addressInfoField >=> addressInfoFields.city
      val streetField = addressInfoField >=> addressInfoFields.street
      val homeNumberField = addressInfoField >=> addressInfoFields.homeNumber
      val homeNumberAdditionField = addressInfoField >=> addressInfoFields.homeAddition
      val flatNumberField = addressInfoField >=> addressInfoFields.flatNumber

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
      val pickUpPointId = orderFields.delivery >=> deliveryFields.pickUpDelivery >=> pickUpDeliveryFields.pickUpPointId
      pickUpPointId.validateRequired.ifPassed {
        pickUpPointId.validateUUID
      }
    } else if (orderAdd.delivery.isPostalDelivery) {
      val postalDeliveryField = orderFields.delivery >=> deliveryFields.postalDelivery
      val zipCodeField = postalDeliveryField >=> postalDeliveryFields.zipCode
      val countryField = postalDeliveryField >=> postalDeliveryFields.country
      val cityField = postalDeliveryField >=> postalDeliveryFields.city

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
