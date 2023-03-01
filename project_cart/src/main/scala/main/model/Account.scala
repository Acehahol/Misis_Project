package main.model

import java.util.UUID

case class Account(id: UUID = UUID.randomUUID(), firstname: String, surname: String, cash: Int = 0)

case class CreateAcc(firstname: String, surname: String)

case class Transfercash(id_1: UUID, id_2: UUID, amount: Int)

case class Transaction(id: UUID, amount: Int)

