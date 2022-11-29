package misis.cart

import misis.cart.model._
import misis.cart.repository.ItemRepositoryInMemory

object CartApp extends App {
  val repository = new ItemRepositoryInMemory

  repository.createItem(CreateItem("apple", 100))
  val cupId = repository.createItem(CreateItem("cup", 50)).id
  val pen = repository.createItem(CreateItem("pen", 200))
  repository.updateItem(UpdateItem(pen.id, 230))
  repository.deleteItem(cupId)

  println(repository.list())
}