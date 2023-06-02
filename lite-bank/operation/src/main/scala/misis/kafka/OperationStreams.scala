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
        .filter(event => event.transaction == 1 || event.transaction == 2)
        .map { command =>
            if (command.transaction == 1) {
                produceCommand(AccountUpdate(command.directId, -command.value, 2, command.accountId, command.category ))
                println(
                    s"С ${command.accountId} счета успешно переведено ${-command.value} на ${command.directId} счет"
                )
            } else {
                produceCommand(
                    AccountUpdated(
                        accountId = command.directId,
                        value = command.value,
                        transaction = 3,
                        directId = command.accountId,
                        category = command.category
                    )
                )
            }
        }
        .to(Sink.ignore)
        .run()
}
