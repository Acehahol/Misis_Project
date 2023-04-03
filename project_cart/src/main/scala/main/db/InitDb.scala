package main.db

import com.typesafe.config.ConfigFactory
import main.db.AccountDb.{itemTable}
import main.model.Account

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._

import java.util.UUID

class InitDb(implicit val ec: ExecutionContext, db: Database) {
    def prepare(): Future[_] = {
        db.run(itemTable.schema.createIfNotExists)
    }

}
