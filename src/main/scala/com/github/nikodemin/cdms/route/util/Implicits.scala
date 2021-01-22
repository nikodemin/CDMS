package com.github.nikodemin.cdms.route.util

import com.github.nikodemin.cdms.model.dto._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import sttp.tapir.openapi.{Schema, SchemaType}

object Implicits {
  implicit val deliveryEncoder: Encoder[DeliveryDto] = deriveEncoder[DeliveryDto]
  implicit val deliveryDecoder: Decoder[DeliveryDto] = deriveDecoder[DeliveryDto]
  implicit val personInfoEncoder: Encoder[PersonInfoDto] = deriveEncoder[PersonInfoDto]
  implicit val personInfoDecoder: Decoder[PersonInfoDto] = deriveDecoder[PersonInfoDto]
  implicit val addressInfoEncoder: Encoder[AddressInfoDto] = deriveEncoder[AddressInfoDto]
  implicit val addressInfoDecoder: Decoder[AddressInfoDto] = deriveDecoder[AddressInfoDto]
  implicit val paymentInfoEncoder: Encoder[PaymentInfoDto] = deriveEncoder[PaymentInfoDto]
  implicit val paymentInfoDecoder: Decoder[PaymentInfoDto] = deriveDecoder[PaymentInfoDto]
  implicit val paymentMethodEncoder: Encoder[PaymentMethodDto] = deriveEncoder[PaymentMethodDto]
  implicit val paymentMethodDecoder: Decoder[PaymentMethodDto] = deriveDecoder[PaymentMethodDto]
  implicit val orderEncoder: Encoder[OrderDto] = deriveEncoder[OrderDto]
  implicit val orderDecoder: Decoder[OrderDto] = deriveDecoder[OrderDto]

  implicit val orderSchema: Schema = {
    val schema: Schema = Schema(SchemaType.Object)
    schema.
      schema
  }
}
