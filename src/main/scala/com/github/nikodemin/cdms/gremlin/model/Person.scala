package com.github.nikodemin.cdms.gremlin.model

import gremlin.scala.id

sealed trait Person

object Person {

  case class StoreKeeper(
                          firstName: String,
                          lastName: String,
                          middleName: Option[String],
                          age: Int,
                          email: String,
                          password: String,
                          phoneNumber: String,
                          @id id: Option[Long] = None
                        ) extends Person

  case class Customer(
                       firstName: String,
                       lastName: String,
                       middleName: Option[String],
                       age: Int,
                       email: String,
                       password: String,
                       phoneNumber: String,
                       avatar: Option[Image],
                       bio: Option[String],
                       status: Option[String],
                       addresses: List[AddressInfo],
                       @id id: Option[Long] = None
                     ) extends Person

  case class Courier(
                      firstName: String,
                      lastName: String,
                      middleName: Option[String],
                      age: Int,
                      email: String,
                      password: String,
                      phoneNumber: String,
                      @id id: Option[Long]
                    ) extends Person

  case class Admin(
                    firstName: String,
                    lastName: String,
                    middleName: Option[String],
                    age: Int,
                    email: String,
                    password: String,
                    @id id: Option[Long]
                  ) extends Person

  case class Manager(
                      firstName: String,
                      lastName: String,
                      middleName: Option[String],
                      age: Int,
                      email: String,
                      password: String,
                      phoneNumber: String,
                      @id id: Option[Long]
                    ) extends Person
}
