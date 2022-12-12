package main.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import main.model.{CreateAcc, Transaction, Transfercash}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import main.repository.{BiggerException, CartRepository}

import scala.util.{Failure, Success}

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
          onSuccess(bank.transfer(del)) {
            case Right(value) => complete(value)
            case Left(s) => complete(StatusCodes.NotAcceptable, s)
          }
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
          onSuccess(bank.deposit(Transaction(id, amount))) {
            case Right(value) => complete(value)
            case Left(s) => complete(StatusCodes.NotAcceptable, s)
          }
        }
      } ~
      //Снятие
      path("cart" / JavaUUID / "takes") { id =>
        (put & entity(as[Int])) { amount =>
          onSuccess(bank.takes(Transaction(id, amount))) {
            case Right(value) => complete(value)
            case Left(s) => complete(StatusCodes.NotAcceptable, s )
          }
        }
      }
  }
}
