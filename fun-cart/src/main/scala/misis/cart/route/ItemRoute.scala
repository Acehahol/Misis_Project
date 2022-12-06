package misis.cart.route

import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.cart.model.{CreateItem, UpdateItem}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import misis.cart.repository.CartRepository

class ItemRoute(repository: CartRepository) extends FailFastCirceSupport {
  def route =
    (path("items") & get) {
      val list = repository.list()
      complete(list)
    } ~
      path("item") {
        (post & entity(as[CreateItem])) { newItem =>
          complete(repository.create(newItem))
        }
      } ~
      path("item" / JavaUUID) { id =>
        get {
          complete(repository.get(id))
        }
      } ~
      path("item") {
        (put & entity(as[UpdateItem])) { updateItem =>
          complete(repository.update(updateItem))
        }
      } ~
      path("item" / JavaUUID) { id =>
        delete {
          complete(repository.delete(id))
        }
      }
}