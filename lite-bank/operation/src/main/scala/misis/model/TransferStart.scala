package misis.model

case class TransferStart (sourceId: Int, destinationId: Int, value: Int , category: String = "Non")
