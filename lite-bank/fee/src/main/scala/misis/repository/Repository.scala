package misis.repository

import akka.actor.ActorSystem
import misis.model.{FeeLimit}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

class FeeRepository()(implicit
    val system: ActorSystem,
    executionContext: ExecutionContext
) {

    private val fee_limits: ListBuffer[FeeLimit] = ListBuffer.empty[FeeLimit]

    def addAccount(accountId: Int): Future[FeeLimit] = {
        val account = FeeLimit(accountId, 5000, proc = 0.1)
        fee_limits += account
        Future.successful(account)
    }

    def getLmBalance(accountId: Int): Int = {
        fee_limits.find(_.id == accountId).map(_.amount).getOrElse(5000)
    }

    def getProc(accountId: Int): Double = {
        fee_limits.find(_.id == accountId).map(_.proc).getOrElse(0)
    }
    def containsAccount(accountId: Int): Boolean = {
        fee_limits.exists(_.id == accountId)
    }

    def updateAccount(accountId: Int, value: Int): Future[FeeLimit] = {
        val accountOption = fee_limits.find(_.id == accountId)
        accountOption match {
            case Some(account) =>
                val updatedAccount = account.update(value)
                fee_limits -= account
                fee_limits += updatedAccount
                Future.successful(updatedAccount)
            case None =>
                throw new IllegalArgumentException(s"Аккаунт с таким ID $accountId не найден")
        }
    }

}
