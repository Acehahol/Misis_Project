package main.repository

import main.model.{Account, CreateAcc, Transaction, Transfercash}

import java.util.UUID
import scala.concurrent.Future

trait CartRepository {
  def list(): Future[Seq[Account]]

  def get(id: UUID): Future[Account]

  def create(cart: CreateAcc):Future[Account]

  def transfer(carts: Transfercash): Future[Option[Account]]

  def deposit(carts: Transaction): Future[Option[Account]]

  def takes(carts: Transaction): Future[Option[Account]]

  def delete(id: UUID): Future[Unit]
}
