package com.github.nikodemin.cdms.dao.util

import org.neo4j.driver.Session

object DaoSpecUtil {

  def clearDb(implicit session: Session): Unit = {
    session.run("match (a) -[r] -> () delete a, r")
    session.run("match (a) delete a")
  }
}
