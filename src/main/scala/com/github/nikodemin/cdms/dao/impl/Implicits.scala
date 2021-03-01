package com.github.nikodemin.cdms.dao.impl

import org.neo4j.driver.{Result, Session}
import zio.{Task, ZIO}

import scala.util.Try

object Implicits {

  implicit class CypherInterpolator(sc: StringContext) {
    def c(subs: Any*)(implicit session: Session): Result = {
      val pit = sc.parts.iterator
      val sit = subs.iterator
      val sb = new java.lang.StringBuilder(pit.next())

      while (sit.hasNext) {
        sb.append(sit.next().toString)
        sb.append(pit.next())
      }

      val tx = session.beginTransaction()

      val result = Try(tx.run(sb.toString))
      if (result.isSuccess) {
        tx.commit()
        tx.close()
        result.get
      } else {
        tx.close()
        throw result.failed.get
      }
    }
  }

  implicit def toZIO[T](res: T): Task[T] = ZIO.effect(res)
}
