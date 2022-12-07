package main.route

import akka.http.scaladsl.server.Directives._
import main.model.{CreateAcc, Transaction, Transfercash}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import main.repository.CartRepository

class ItemRoute(bank: CartRepository) extends FailFastCirceSupport {
  def route = {
    //Посмотреть все счета
    (path("carts") & get) {
      val list = bank.list()
      complete(list)
    } ~
      //Создать счет
      path("add") {
        (post & entity(as[CreateAcc])) { newCart =>
          complete(bank.create(newCart))
        }
      } ~
      //Получить данные
      path("cart" / JavaUUID) { id =>
        get {
          complete(bank.get(id))
        }
      } ~
      //path("cart" / JavaUUID ) { id =>
      //  (put & entity(as[Transfercash])) { deliver =>
      //    complete(bank.transfer(Transfercash(id, deliver.id_2, deliver.amount)))
      //  }
      //} ~
      path("transfer") {
        (put & entity(as[Transfercash])) { del =>
          complete(bank.transfer(del))
        }
      } ~
      //Удалить счет
      path("cart" / JavaUUID) { id =>
        delete {
          complete(bank.delete(id))
        }
      } ~
      //Положить средства
      path("cart" / JavaUUID / "deposit") { id =>
        (put & entity(as[Int])) { amount =>
          complete(bank.deposit(Transaction(id, amount)))
        }
      } ~
      //Снятие
      path("cart" / JavaUUID / "takes") { id =>
        (put & entity(as[Int])) { amount =>
          complete(bank.takes(Transaction(id, amount)))
        }
      }
  }
}
