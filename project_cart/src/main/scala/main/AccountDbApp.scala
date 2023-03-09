package main

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import main.db.InitDb
import main.repository.AccountRepositoryDb
import main.route.AccountRoute
import slick.jdbc.PostgresProfile.api._
import main.repository._

import scala.concurrent.ExecutionContextExecutor

object AccountDbApp extends App {
    implicit val system: ActorSystem = ActorSystem("CartApp")
    implicit val ec: ExecutionContextExecutor = system.dispatcher
    implicit val db = Database.forConfig("database.postgres")
    val port = ConfigFactory.load().getInt("port")
    new InitDb().prepare()
    val client = new TranferClient
    val bank = new AccountRepositoryDb(client)
    val itemroute = new AccountRoute(bank).route

    Http().newServerAt("0.0.0.0", port).bind(itemroute)

}
