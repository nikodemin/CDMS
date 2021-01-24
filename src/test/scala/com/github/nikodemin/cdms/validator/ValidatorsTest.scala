package com.github.nikodemin.cdms.validator

import java.time.LocalDate
import java.util.UUID

import akka.event.slf4j.SLF4JLogging
import com.github.nikodemin.cdms.proto.OrderAdd.Delivery
import com.github.nikodemin.cdms.proto.PaymentInfo.PaymentMethod
import com.github.nikodemin.cdms.proto._
import org.scalacheck.Gen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class ValidatorsTest extends AnyWordSpecLike
  with Matchers
  with ScalaCheckPropertyChecks
  with SLF4JLogging {
  val currencies = Set("RUB", "EUR", "USD")

  val localDateGen: Gen[LocalDate] = {
    val rangeStart = LocalDate.now().toEpochDay
    val rangeEnd = LocalDate.now().plusYears(100).toEpochDay
    Gen.choose(rangeStart, rangeEnd).map(i => LocalDate.ofEpochDay(i))
  }
  val uuidSeqGen: Gen[Seq[UUID]] = Gen.nonEmptyListOf(Gen.uuid)
  val currencyGen: Gen[Option[String]] = Gen.option(Gen.oneOf(currencies))
  val notBlankStringGen: Gen[String] = Gen.alphaNumStr.filterNot(_.isBlank)
  val cvvGen: Gen[Int] = Gen.posNum[Int].filter(_.toString.length == 3)


  "OrderValidation" should {
    "pass right orders" in {
      forAll {
        for {
          localDate <- localDateGen
          uuidSeq <- uuidSeqGen
          currency <- currencyGen
          positiveInt <- Gen.posNum[Int]
          positiveLong <- Gen.posNum[Long]
          notBlankString <- notBlankStringGen
          cvv <- cvvGen
          uuid <- Gen.uuid
        } yield (localDate, uuidSeq, currency, positiveInt, positiveLong, notBlankString, cvv, uuid)
      } { case (localDate, uuidSeq, currency, positiveInt, positiveLong, notBlankString, cvv, uuid) =>
        val creditCard = CreditCard(notBlankString, notBlankString, localDate.toString, cvv)
        val paymentInfoCard = PaymentInfo(positiveLong, currency, PaymentMethod.CARD, Some(creditCard))
        val paymentInfoCash = PaymentInfo(positiveLong, currency, PaymentMethod.CASH)
        val postalDelivery = Delivery.PostalDelivery(PostalDelivery(notBlankString, Some(notBlankString), Option(notBlankString)))
        val courierDelivery = Delivery.CourierDelivery(CourierDelivery(AddressInfoAdd(notBlankString, notBlankString, notBlankString, positiveInt, None, positiveInt)))
        val pickUpDelivery = Delivery.PickUpDelivery(PickUpDelivery(uuid.toString))

        forAll {
          for {
            delivery <- Gen.oneOf(postalDelivery, courierDelivery, pickUpDelivery)
            paymentInfo <- Gen.oneOf(paymentInfoCard, paymentInfoCash)
          } yield (delivery, paymentInfo)
        } { case (delivery, paymentInfo) =>
          val orderAdd = OrderAdd(paymentInfo, delivery, uuid.toString, uuidSeq.map(_.toString))

          log.info(s"test valid order: ${orderAdd.toProtoString}")

          Validators.validateOrder(orderAdd) should have size 0
        }
      }
    }
  }
}
