package com.github.nikodemin.cdms.actor

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import com.github.nikodemin.cdms.proto.OrderAdd


object OrderActor {

  sealed trait Command

  case class Add(order: OrderAdd) extends Command


  sealed trait Event

  case class OrderAdded(order: OrderAdd) extends Event


  sealed trait State

  object Empty extends State

  object AcceptingOrders extends State


  def apply(entityId: String): Behavior[Command] = Behaviors.setup { ctx =>
    val commandHandler: (State, Command) => Effect[Event, State] = (state, cmd) => {
      cmd match {
        case Add(order) => Effect.none
      }
    }

    val eventHandler: (State, Event) => State = (state, event) => event match {
      case OrderAdded(order) => AcceptingOrders
    }


    EventSourcedBehavior(
      PersistenceId("OrderActor", entityId),
      Empty,
      commandHandler,
      eventHandler
    )
  }
}
