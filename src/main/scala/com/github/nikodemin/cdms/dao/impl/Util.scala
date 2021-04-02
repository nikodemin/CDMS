package com.github.nikodemin.cdms.dao.impl

import com.github.nikodemin.cdms.model.exceptions.EntityIdNotFound
import gremlin.scala.ScalaGraph
import org.apache.tinkerpop.gremlin.structure.Element
import zio.{IO, Task, ZIO}

object Util {
  def exec[A](block: => A)(implicit graph: ScalaGraph): Task[A] = ZIO.effect {
    graph.tx.open()
    block
  }.bimap(ex => {
    graph.tx.rollback()
    ex
  }, res => {
    graph.tx.commit()
    res
  })

  def getElemId(element: Element): IO[EntityIdNotFound, Long] = element.id() match {
    case Long@id => ZIO.succeed(id.asInstanceOf[Long])
    case _ => ZIO.fail(EntityIdNotFound(s"${element.label()} id is not an instance of Long"))
  }
}
