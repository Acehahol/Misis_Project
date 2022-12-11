package main

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import main.db.InitDb
import main.repository.CartRepositoryDb
import main.route.ItemRoute
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContextExecutor

object CartDbApp extends App {
  implicit val system: ActorSystem = ActorSystem("CartApp")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val db = Database.forConfig("database.postgres")

  new InitDb().prepare()
  val bank = new CartRepositoryDb
  val itemroute = new ItemRoute(bank).route

  Http().newServerAt("0.0.0.0", 8080).bind(itemroute)

}
