package misis.model

import java.util.UUID

case class Account(id: Int, amount: Int) {
    def update(value: Int) = this.copy(amount = amount + value)
}
case class CashBack(id: Int, amount: Int) {
    def update(value: Int) = this.copy(amount = amount + value)
}
case class FeeLimit(id: Int, amount: Int, proc: Double) {
    def update(value: Int) = this.copy(amount = amount + value)
}
trait Command
case class AccountUpdate(accountId: Int, value: Int, transaction: Int = 0, directId: Int = 0 )

case class AccountCreate(accountId: Int)

case class CashBackUpdate(accountId: Int, value: Int)

trait Event
case class AccountUpdated(
                             accountId: Int,
                             value: Int,
                             transaction: Int,
                             directId: Int,
                         )
case class AccountCreated(accountId: Int)

case class CashBackUpdated(accountId: Int, value: Int)