package com.github.nikodemin.cdms

import org.neo4j.driver.{AuthTokens, Driver, GraphDatabase, Session}
import scalapb.zio_grpc.{ServerMain, ServiceList}
import zio.Runtime

object Main extends ServerMain {
  val runtime = Runtime.default
  val driver: Driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "12345"))
  val session: Session = driver.session

  override def services: ServiceList[zio.ZEnv] = ???
}
