package main.repository

import main.model.{Account, CreateAcc, Transaction, Transfercash}
import java.util.UUID
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class AccountRepositoryM(client: TranferClient)(implicit val ec: ExecutionContext) extends AccountRepository {
    private val bank = mutable.Map[UUID, Account]()

    override def list(): Future[Seq[Account]] = Future {
        bank.values.toList
    }

    override def get(id: UUID): Future[Account] = Future {
        bank(id)
    }

    override def create(create: CreateAcc): Future[Account] = Future {
        val cart = Account(id = UUID.randomUUID(), firstname = create.firstname, surname = create.surname)
        bank.put(cart.id, cart)
        cart
    }

    override def transfer(carts: Transfercash): Future[Either[String, Account]] = {
        for {
            future <- takes(Transaction(carts.id_1, carts.amount))
            nextstep = future match {
                case Right(account) => deposit(Transaction(carts.id_2, carts.amount))
                case Left(s) => Future.successful(Left(s))
            }
            res <- nextstep
        } yield res
    }

    override def transfer_other(carts: Transfercash): Future[Account] = {
        for {
            future <- takes(Transaction(carts.id_1, carts.amount))
            res <- client.deposit_other(Transaction(carts.id_2, carts.amount))
        } yield res
    }
    override def deposit(carts: Transaction): Future[Either[String, Account]] = Future {
        bank.get(carts.id)
            .map { cart =>
                val up_cart = cart.copy(cash = cart.cash + carts.amount)
                bank.put(cart.id, up_cart)
                Right(up_cart)
            }
            .getOrElse(Left("Не найден элемент"))
    }

    override def takes(carts: Transaction): Future[Either[String, Account]] = Future {
        bank.get(carts.id)
            .map { cart =>
                val up_cart = cart.copy(cash = cart.cash - carts.amount)
                bank.put(cart.id, up_cart)
                Right(up_cart)
            }
            .getOrElse(Left("Не найден элемент"))
    }

    override def delete(id: UUID): Future[Unit] = Future {
        bank.remove(id)
    }
}
