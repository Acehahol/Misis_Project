package main

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import main.model.{CreateAcc, Transaction, Transfercash}
import main.repository.CartRepositoryM
import main.route.ItemRoute

object CartHttp extends App {
  implicit val system: ActorSystem = ActorSystem("CartApp")
  val bank = new CartRepositoryM
  val itemroute = new ItemRoute(bank).route

  Http().newServerAt("0.0.0.0", 8080).bind(itemroute)

}
