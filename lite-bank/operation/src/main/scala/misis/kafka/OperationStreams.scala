package misis.kafka

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import misis.{TopicName, WithKafka}
import misis.model.{AccountUpdate, AccountUpdated}
import io.circe.generic.auto._
import scala.concurrent.ExecutionContext

class OperationStreams()(implicit val system: ActorSystem, executionContext: ExecutionContext) extends WithKafka {
    override def group: String = "operation"
    implicit val commandTopicName: TopicName[AccountUpdated] = simpleTopicName[AccountUpdated]

    kafkaSource[AccountUpdated]
        .filter(event => event.transaction == 1)
        .map { command =>
            produceCommand(AccountUpdate(command.directId, -command.value))
            println(s"С ${command.accountId} счета успешно переведено ${-command.value} на ${command.directId} счет")
        }
        .to(Sink.ignore)
        .run()
}
