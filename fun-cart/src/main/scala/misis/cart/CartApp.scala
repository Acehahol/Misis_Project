package misis.cart

import misis.cart.model._
import misis.cart.repository.ItemRepositoryInMemory
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

object CartApp extends App {
  val repository = new ItemRepositoryInMemory

  repository.createItem(CreateItem("apple", 100))
  val cupId = repository.createItem(CreateItem("cup", 50)).id
  val pen = repository.createItem(CreateItem("pen", 200))
  repository.updateItem(UpdateItem(pen.id, 230))
  repository.deleteItem(cupId)

  private val list = repository.list()
  val result = list.asJson.spaces2
  println(result)
}