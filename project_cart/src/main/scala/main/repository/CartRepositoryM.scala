package main.repository
import main.model.{Account, CreateAcc, Transaction, Transfercash}

import java.util.UUID
import scala.collection.mutable

class CartRepositoryM extends CartRepository {
  private val bank = mutable.Map[UUID, Account]()
  override def list(): scala.List[Account] = {
    bank.values.toList
  }

  override def get(id: UUID): Account = ???

  override def create(create: CreateAcc): Account = {
    val cart = Account(id = UUID.randomUUID(), firstname = create.firstname, surname = create.surname)
    bank.put(cart.id, cart)
    cart
  }

  override def transfer(carts: Transfercash): Option[Account] = {
    takes(Transaction(carts.id_1, carts.amount))
    deposit(Transaction(carts.id_2, carts.amount))
  }

  override def deposit(carts: Transaction): Option[Account] = {
    bank.get(carts.id).map { cart =>
      val up_cart = cart.copy(cash = cart.cash + carts.amount)
      bank.put(cart.id, up_cart)
      up_cart
    }
  }

  override def takes(carts: Transaction): Option[Account] = {
    bank.get(carts.id).map { cart =>
      val up_cart = cart.copy(cash = cart.cash - carts.amount)
      bank.put(cart.id, up_cart)
      up_cart
    }
  }

  override def delete(id: UUID): Unit = {
    bank.remove(id)
  }
}
