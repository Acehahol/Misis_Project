package misis.cart.repository

import misis.cart.model.{CreateItem, Item, UpdateItem}

import java.util.UUID
import scala.collection.mutable

class ItemRepositoryInMemory extends ItemRepository {
  private val store = mutable.Map[UUID, Item]()

  override def list(): scala.List[Item] = {
    store.values.toList
  }

  override def createItem(create: CreateItem): Item = {
    val item = Item(id = UUID.randomUUID(), name = create.name, price = create.price)
    store.put(item.id, item)
    item
  }

  override def updateItem(update: UpdateItem): Option[Item] = {
    store.get(update.id).map { item =>
      val updated = item.copy(price = update.price)
      store.put(item.id, updated)
      updated
    }
  }

  override def deleteItem(id: UUID) = {
    store.remove(id)
  }
}
