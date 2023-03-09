package main

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import main.model.{CreateAcc, Transaction, Transfercash}
import main.repository.{AccountRepositoryM, TranferClient}
import main.route.AccountRoute

import scala.concurrent.ExecutionContextExecutor

object AccountMemoryApp extends App {
    implicit val system: ActorSystem = ActorSystem("CartApp")
    implicit val ec: ExecutionContextExecutor = system.dispatcher
    val client = new TranferClient
    val bank = new AccountRepositoryM(client)
    val itemroute = new AccountRoute(bank).route

    Http().newServerAt("0.0.0.0", 8080).bind(itemroute)

}
