package main

import main.model.{CreateAcc, Transaction, Transfercash}
import main.repository.CartRepositoryM

object CartApp extends App{
  val bank = new CartRepositoryM
  val card_1 = bank.create(CreateAcc("Vlad","Pehotin"))
  val card_2 = bank.create(CreateAcc("Roma","Yuvakaev"))
  bank.deposit(Transaction(card_1.id, 200))
  bank.transfer(Transfercash(card_1.id, card_2.id, 150))


  println(bank.list())

}
