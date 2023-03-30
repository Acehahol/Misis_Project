package main.db

import main.model.Account
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import java.util.UUID

object AccountDb {
    class ItemTable(tag: Tag) extends Table[Account](tag, "Accounts") {
        val id = column[UUID]("id", O.PrimaryKey)
        val firstname = column[String]("firstname")
        val surname = column[String]("surname")
        val cash = column[Int]("cash")
        val cashback = column[Int]("cashback")
        val selected_category = column[String]("selected_category")

        def * =
            (id, firstname, surname, cash, cashback, selected_category) <> ((Account.apply _).tupled, Account.unapply)
    }

    val itemTable = TableQuery[ItemTable]
}
