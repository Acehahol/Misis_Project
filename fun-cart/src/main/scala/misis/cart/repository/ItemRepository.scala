package misis.cart.repository

import misis.cart.model._

import java.util.UUID

trait ItemRepository {
  def list(): List[Item]
  def createItem(item: CreateItem): Item
  def updateItem(item: UpdateItem): Option[Item]
  def deleteItem(id: UUID)
}
