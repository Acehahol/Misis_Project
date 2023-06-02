package misis.model

import java.util.UUID

case class Account(id: Int, amount: Int) {
    def update(value: Int) = this.copy(amount = amount + value)
}
case class CashBack(id: Int, amount: Int) {
    def update(value: Int) = this.copy(amount = amount + value)
}
case class Category(name: String, proc: Double)

trait Command
case class AccountUpdate(accountId: Int, value: Int, transaction: Int = 0, directId: Int = 0, category: String = "Non")

case class AccountCreate(accountId: Int)

case class CashBackUpdate(accountId: Int, value: Int, category: String)


trait Event
case class AccountUpdated(
                             accountId: Int,
                             value: Int,
                             transaction: Int,
                             directId: Int,
                             category: String
                         )
case class AccountCreated(accountId: Int)

case class CashBackUpdated(accountId: Int, valueCb: Int, category: String)

