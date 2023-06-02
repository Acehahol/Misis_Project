package misis.repository

import akka.stream.scaladsl.Sink
import io.circe.generic.auto._
import misis.TopicName
import misis.kafka.OperationStreams
import misis.model.{AccountUpdate, AccountUpdated, TransferStart}
import akka.actor.ActorSystem
import scala.concurrent.{ExecutionContext, Future}
import java.util.UUID

class Repository(streams: OperationStreams)(implicit val system: ActorSystem, executionContext: ExecutionContext) {
    implicit val commandTopicName: TopicName[AccountUpdate] = streams.simpleTopicName[AccountUpdate]
    implicit val accountUpdatedTopicName: TopicName[AccountUpdated] = new TopicName[AccountUpdated] {
        override def get: String = "AccountUpdated"
    }

    def transfer(transfer: TransferStart) = {
        if (transfer.value > 0) {
            streams.produceCommand(
                AccountUpdate(transfer.sourceId, -transfer.value, 1, transfer.destinationId, transfer.category)
            )
        }
    }
}
