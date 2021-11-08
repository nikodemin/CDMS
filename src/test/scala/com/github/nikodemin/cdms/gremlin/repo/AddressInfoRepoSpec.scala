package com.github.nikodemin.cdms.gremlin.repo

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.implicits.catsSyntaxOptionId
import com.dimafeng.testcontainers.{ForAllTestContainer, Neo4jContainer}
import com.github.nikodemin.cdms.api.Types.AddressInfoCriteria
import com.github.nikodemin.cdms.gremlin.ElementIdProvider
import com.steelbridgelabs.oss.neo4j.structure.{Neo4JGraphConfigurationBuilder, Neo4JGraphFactory}
import gremlin.scala.{ScalaGraph, asScalaGraph}
import org.apache.commons.configuration.Configuration
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import org.testcontainers.utility.DockerImageName

class AddressInfoRepoSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with ForAllTestContainer {

  override val container: Neo4jContainer =
    Neo4jContainer(neo4jImageVersion = DockerImageName.parse("neo4j:4.1.9"))

  var config: Configuration = _

  override def afterStart =
    config = Neo4JGraphConfigurationBuilder.connect(
      container.host,
      container.exposedPorts.head.toShort,
      container.username,
      container.password
    ).withElementIdProvider(classOf[ElementIdProvider])
      .build

  implicit lazy val graph: ScalaGraph = Neo4JGraphFactory.open(config)
  lazy val repoIO = AddressInfoRepo.live[IO](config)

  "AddressInfoRepo" - {
    "search" in {
      (for {
        repo <- repoIO
        criteria = AddressInfoCriteria(country = "USA".some, None, None, None, None, None)
        res <- repo.search(criteria)
      } yield res).asserting(_.isRight shouldBe true)
    }
  }
}
