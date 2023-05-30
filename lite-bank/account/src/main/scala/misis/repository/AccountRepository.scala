package misis.repository

import misis.model.Account
import scala.concurrent.Future
import scala.collection.mutable.ListBuffer

class AccountRepository() {

    private val accounts: ListBuffer[Account] = ListBuffer.empty[Account]

    def addAccount(accountId: Int): Future[Account] = {
        val account = Account(accountId, 0)
        accounts += account
        Future.successful(account)
    }

    def getAccountBalance(accountId: Int): Int = {
        accounts.find(_.id == accountId).map(_.amount).getOrElse(0)
    }

    def containsAccount(accountId: Int): Boolean = {
        accounts.exists(_.id == accountId)
    }
    def updateAccount(accountId: Int, value: Int): Future[Account] = {
        val accountOption = accounts.find(_.id == accountId)
        accountOption match {
            case Some(account) =>
                val updatedAccount = account.update(value)
                accounts -= account
                accounts += updatedAccount
                Future.successful(updatedAccount)
            case None =>
                throw new IllegalArgumentException(s"Аккаунт с таким ID $accountId не найден")
        }
    }
}
