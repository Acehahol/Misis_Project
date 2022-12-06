package main.repository

import main.model.{Account, CreateAcc, Transaction, Transfercash}

import java.util.UUID
import scala.concurrent.Future

trait CartRepository {
  def list(): List[Account]

  def get(id: UUID): Account

  def create(cart: CreateAcc): Account

  def transfer(carts: Transfercash):  Option[Account]

  def deposit(carts: Transaction): Option[Account]

  def takes(carts: Transaction): Option[Account]

  def delete(id: UUID): Unit
}
