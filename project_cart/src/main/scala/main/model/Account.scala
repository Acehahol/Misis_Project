package main.model

import java.util.UUID

case class Account(
    id: UUID = UUID.randomUUID(),
    firstname: String,
    surname: String,
    cash: Int = 0,
    cashback: Int = 0,
    selected_category: String
)

case class CreateAcc(firstname: String, surname: String, selected_category: String)

case class Transfercash(id_1: UUID, id_2: UUID, amount: Int, category: String)

case class Transaction(id: UUID, amount: Int, category: String = "takes_deposit")
