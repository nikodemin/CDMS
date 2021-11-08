package com.github.nikodemin.cdms.gremlin.repo

import cats.effect.Async
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId, toFunctorOps, _}
import com.github.nikodemin.cdms.api.Types.{AddressInfoCriteria, Pagination}
import com.github.nikodemin.cdms.gremlin.model.AddressInfo
import gremlin.scala.GremlinScala.Aux
import gremlin.scala._
import org.apache.commons.configuration.Configuration
import shapeless.HNil

import scala.concurrent.Future

trait AddressInfoRepo[F[_]] {

  def search(
              criteria: AddressInfoCriteria,
              pagination: Option[Pagination] = None
            ): F[Either[IllegalArgumentException, List[AddressInfo]]]
}

object AddressInfoRepo {

  def live[F[_] : Async](configuration: Configuration)(implicit graph: ScalaGraph): F[AddressInfoRepo[F]] = new AddressInfoRepo[F] {

    override def search(
                         criteria: AddressInfoCriteria,
                         pagination: Option[Pagination]
                       ): F[Either[IllegalArgumentException, List[AddressInfo]]] =
      graph.traversal.withRemote(configuration).V
        .hasLabel[AddressInfo]
        .withOptLabel(criteria.city, "city")
        .withOptLabel(criteria.street, "street")
        .withOptLabel(criteria.country, "country")
        .withOptLabel(criteria.homeNumber, "homeNumber")
        .withOptLabel(criteria.homeNumberAddition, "homeNumberAddition")
        .withOptLabel(criteria.flatNumber, "flatNumber")
        .withOptPagination(pagination)
        .map {
          _.promise
            .toAsync[F]
            .map(_.map(_.toCC[AddressInfo]))
        }.traverse(identity)
  }.pure[F]

  implicit class VertexOps(private val vertex: Aux[Vertex, HNil]) extends AnyVal {

    def withOptLabel[T](opt: Option[T], keyName: String): Aux[Vertex, HNil] =
      opt.fold(vertex)(v => vertex.has(Key[T](keyName), v))

    def withOptPagination(pagination: Option[Pagination]): Either[IllegalArgumentException, Aux[Vertex, HNil]] =
      pagination.fold(vertex.asRight[IllegalArgumentException]) {
        case Pagination(Some(offset), Some(quantity)) => vertex.range(offset, offset + quantity).asRight
        case Pagination(None, Some(quantity)) => vertex.range(0, quantity).asRight
        case Pagination(Some(_), None) => new IllegalArgumentException("Must set quantity, when setting offset").asLeft
        case Pagination(None, None) => new IllegalArgumentException("Empty pagination").asLeft
      }
  }

  implicit class FutureOps[T](private val f: Future[T]) extends AnyVal {
    def toAsync[F[_] : Async]: F[T] = Async[F].fromFuture(f.pure[F])
  }
}
