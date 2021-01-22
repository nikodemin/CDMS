package com.github.nikodemin.cdms

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.actor.{ActorSystem => ClassicActorSystem}
import akka.http.scaladsl.Http
import akka.stream.alpakka.cassandra.scaladsl.CassandraSessionRegistry
import com.github.nikodemin.cdms.route.OrderRouter
import com.github.nikodemin.cdms.service.OrderService

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

object Main {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem(Behaviors.empty, "cdms-cluster")
    implicit val classicSystem: ClassicActorSystem = system.toClassic
    import classicSystem.dispatcher

    val orderService = new OrderService

    val orderRouter = new OrderRouter(orderService)

    val routes = orderRouter.route

    val binding = Http().newServerAt("localhost", 9002)
      .bind(routes)

    binding.foreach(b => println(s"Binding on ${b.localAddress}"))

    StdIn.readLine()

    binding.flatMap(_.unbind()).onComplete(_ => {
      classicSystem.terminate
      system.terminate
    })
  }

  def createTables(system: ActorSystem[_]): Unit = {

    val session =
      CassandraSessionRegistry(system).sessionFor("alpakka.cassandra")

    val keyspaceStmt =
      """
      CREATE KEYSPACE IF NOT EXISTS cdms_offset
      WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 }
      """

    val offsetTableStmt =
      """
      CREATE TABLE IF NOT EXISTS cdms_offset.offset_store (
        projection_name text,
        partition int,
        projection_key text,
        offset text,
        manifest text,
        last_updated timestamp,
        PRIMARY KEY ((projection_name, partition), projection_key)
      )
      """
    Await.ready(session.executeDDL(keyspaceStmt), 10.seconds)
    Await.ready(session.executeDDL(offsetTableStmt), 10.seconds)
  }
}
