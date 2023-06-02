package misis.kafka

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{Sink, Source}
import io.circe.generic.auto._
import io.circe.syntax._
import misis.WithKafka
import misis.model.{AccountUpdate, AccountUpdated, CashBackUpdate, CashBackUpdated}
import misis.repository.FeeRepository
import org.apache.kafka.clients.producer.ProducerRecord

import scala.concurrent.ExecutionContext

class FeeStreams(repository: FeeRepository)(implicit
    val system: ActorSystem,
    executionContext: ExecutionContext
) extends WithKafka {
    override def group: String = "fee"

    kafkaSource[AccountUpdated]
        .filter(event => event.transaction == 3)
        .map { command =>
            if (repository.containsAccount(command.accountId)) {
                if (command.value > repository.getLmBalance(command.accountId)) {
                    produceCommand(
                        AccountUpdate(
                            command.accountId,
                            (-command.value * repository.getProc(command.accountId)).toInt,
                            4
                        )
                    )
                    println(
                    )
                    repository.updateAccount(command.accountId, -command.value)
                } else {
                    repository.updateAccount(command.accountId, -command.value)
                    println(
                    )
                }
            } else {
                repository
                    .addAccount(command.accountId)
                if (command.value > repository.getLmBalance(command.accountId)) {
                    produceCommand(
                        AccountUpdate(
                            command.accountId,
                            (-command.value * repository.getProc(command.accountId)).toInt,
                            4
                        )
                    )
                    println(
                    )
                    repository.updateAccount(command.accountId, -5000)
                } else {
                    repository.updateAccount(command.accountId, -command.value)
                    println(
                    )
                }
            }
        }
        .to(Sink.ignore)
        .run()

}
