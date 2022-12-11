package main.db

import main.db.CartDb.itemTable

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._

class InitDb (implicit val ec :ExecutionContext, db : Database){
  def prepare() : Future[_] = {
    db.run(itemTable.schema.createIfNotExists)
  }

}
