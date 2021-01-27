package com.github.nikodemin.cdms

import com.github.nikodemin.cdms.proto.OrderAdd
import com.github.nikodemin.validation.FieldsMacro

object Test extends App {
  println(FieldsMacro.genFieldsObj[TestClass].iField)
  println(FieldsMacro.genFieldsObj[OrderAdd].paymentInfoField)
}

case class TestClass(i: Int, string: String)
