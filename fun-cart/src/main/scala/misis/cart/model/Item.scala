package misis.cart.model

import java.util.UUID

case class Item(id: UUID, name: String, price: Int)

case class CreateItem(name: String, price: Int)
case class UpdateItem(id: UUID, price: Int)
