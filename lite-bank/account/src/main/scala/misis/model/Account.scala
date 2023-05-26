package misis.model

import java.time.Instant
import java.util.UUID

case class Account(id: Int, amount: Int) {
    def update(value: Int) = this.copy(amount = amount + value)
}

trait Command
case class AccountUpdate(accountId: Int, value: Int, transaction: Int = 0, directId: Int = 0)

trait Event
case class AccountUpdated(
    accountId: Int,
    value: Int,
    transaction: Int,
    directId: Int
)

