package main.repository

import main.db.AccountDb._
import main.model.{Account, CreateAcc, Transaction, Transfercash}
import slick.jdbc.PostgresProfile.api._
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class AccountRepositoryDb(client: TranferClient)(implicit val ec: ExecutionContext, db: Database)
    extends AccountRepository {

    override def list(): Future[Seq[Account]] = {
        db.run(itemTable.result)
    }

    override def get(id: UUID): Future[Account] = {
        db.run(itemTable.filter(_.id === id).result.head)
    }

    def find(id: UUID): Future[Option[Account]] = {
        db.run(itemTable.filter(_.id === id).result.headOption)
    }

    override def create(createacc: CreateAcc): Future[Account] = {
        val acc = Account(firstname = createacc.firstname, surname = createacc.surname)
        for {
            _ <- db.run(itemTable += acc)
            res <- get(acc.id)
        } yield res
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

    override def transfer_other(carts: Transfercash): Future[Either[String, Account]] = {
        for {
            future <- takes(Transaction(carts.id_1, carts.amount))
            nextstep = future match {
                case Right(account) => client.deposit_other(Transaction(carts.id_2, carts.amount))
                case Left(s) => Future.successful(Left(s))
            }
            res <- nextstep
        } yield res
    }

    override def deposit(carts: Transaction): Future[Either[String, Account]] = {

        val query = itemTable
            .filter(_.id === carts.id)
            .map(_.cash)
        for {
            oldcash <- db.run(query.result.headOption)
            cash = carts.amount
            updateCash = oldcash
                .map { oldc =>
                    Right(oldc + cash)
                }
                .getOrElse(Left("Не найден аккаунт"))
            future = updateCash.map(price =>
                db.run {
                    query.update(price)
                }
            ) match {
                case Right(future) => future.map(Right(_))
                case Left(s) => Future.successful(Left(s))
            }
            updated <- future
            res <- find(carts.id)
        } yield updated.map(_ => res.get)
    }

    override def takes(carts: Transaction): Future[Either[String, Account]] = {
        val query = itemTable
            .filter(_.id === carts.id)
            .map(_.cash)
        for {
            oldcash <- db.run(query.result.headOption)
            cash = carts.amount
            updateCash = oldcash
                .map { oldc =>
                    if (oldc - cash < 0)
                        Left("Недостаточно средств")
                    else Right(oldc - cash)
                }
                .getOrElse(Left("Не найден аккаунт"))
            future = updateCash.map(price =>
                db.run {
                    query.update(price)
                }
            ) match {
                case Right(future) => future.map(Right(_))
                case Left(s) => Future.successful(Left(s))
            }
            updated <- future
            res <- find(carts.id)
        } yield updated.map(_ => res.get)
    }

    override def delete(id: UUID): Future[Unit] = {
        db.run(itemTable.filter(_.id === id).delete).map(_ => ())
    }
}

class BiggerException extends Error
