package com.github.nikodemin.cdms.gremlin

import com.steelbridgelabs.oss.neo4j.structure.Neo4JElementIdProvider
import org.neo4j.driver.types.Entity

import java.util.concurrent.atomic.AtomicLong

object ElementIdProvider {
  val IdFieldName = "id"
}

class ElementIdProvider extends Neo4JElementIdProvider[Long] {

  import ElementIdProvider._

  private val atomicLong = new AtomicLong()

  override def fieldName(): String = IdFieldName

  override def generate(): Long = atomicLong.incrementAndGet()

  override def processIdentifier(id: Any): Long = id match {
    case long: Long     => long
    case number: Number => number.longValue()
    case str: String    => str.toLong
    case null           => throw new IllegalArgumentException("Expected an id that is convertible to Long but received null")
    case _              =>
      throw new IllegalArgumentException(s"Expected an id that is convertible to Long but received ${id.getClass}")
  }

  def get(entity: Entity): Long = entity.id

  def matchPredicateOperand(alias: String): String = s"$alias.$IdFieldName"
}
