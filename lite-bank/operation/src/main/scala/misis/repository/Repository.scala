package misis.repository

import io.circe.generic.auto._
import misis.TopicName
import misis.kafka.Streams
import misis.model.{AccountUpdate, TransferStart}
import misis.WithKafka

class Repository(streams: Streams) {
    implicit val commandTopicName: TopicName[AccountUpdate] = streams.simpleTopicName[AccountUpdate]

    def transfer(transfer: TransferStart) = {
        if (transfer.value > 0) {
            streams.produceCommand(AccountUpdate(transfer.sourceId, -transfer.value))
            streams.produceCommand(AccountUpdate(transfer.destinationId, transfer.value))
        }
    }
}
