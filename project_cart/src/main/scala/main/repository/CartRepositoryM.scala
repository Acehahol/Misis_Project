package main.repository

import main.model.{Account, CreateAcc, Transaction, Transfercash}
import java.util.UUID
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class CartRepositoryM(implicit val ec :ExecutionContext) extends CartRepository {
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

  override def transfer(carts: Transfercash): Future[Option[Account]] =  {
    takes(Transaction(carts.id_1, carts.amount))
    deposit(Transaction(carts.id_2, carts.amount))
  }

  override def deposit(carts: Transaction): Future[Option[Account]] = Future {
    bank.get(carts.id).map { cart =>
      val up_cart = cart.copy(cash = cart.cash + carts.amount)
      bank.put(cart.id, up_cart)
      up_cart
    }
  }

  override def takes(carts: Transaction): Future[Option[Account]] = Future {
    bank.get(carts.id).map { cart =>
      val up_cart = cart.copy(cash = cart.cash - carts.amount)
      bank.put(cart.id, up_cart)
      up_cart
    }
  }

  override def delete(id: UUID): Future[Unit] = Future {
    bank.remove(id)
  }
}
