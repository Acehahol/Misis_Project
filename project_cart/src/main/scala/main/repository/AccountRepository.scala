package main.repository

import main.model.{Account, CreateAcc, Transaction, Transfercash}

import java.util.UUID
import scala.concurrent.Future

trait AccountRepository {
    def list(): Future[Seq[Account]]

    def get(id: UUID): Future[Account]

    def create(cart: CreateAcc): Future[Account]

    def transfer(carts: Transfercash): Future[Either[String, Account]]

    def transfer_other(carts: Transfercash): Future[Either[String, Account]]
    def deposit(carts: Transaction): Future[Either[String, Account]]

    def takes(carts: Transaction): Future[Either[String, Account]]

    def delete(id: UUID): Future[Unit]
}
