package misis.cart

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.cart.model._
import misis.cart.repository.ItemRepositoryInMemory
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._


object CartHttpApp extends App with FailFastCirceSupport {
  implicit val system: ActorSystem = ActorSystem("CartApp")
  val repository = new ItemRepositoryInMemory

  repository.createItem(CreateItem("apple", 100))
  val cupId = repository.createItem(CreateItem("cup", 50)).id
  val pen = repository.createItem(CreateItem("pen", 200))
  repository.updateItem(UpdateItem(pen.id, 230))
  repository.deleteItem(cupId)
  val route: Route =
    (path("hello") & get) {
      complete("Hello scala world!")
    } ~
      (path("items") & get) {
        val list = repository.list()
        complete(list)
      } ~
      path("item") {
        (post & entity(as[CreateItem])) { newItem =>
          complete(repository.createItem(newItem))
        }
      }
  Http().newServerAt("0.0.0.0", 8080).bind(route)

}
