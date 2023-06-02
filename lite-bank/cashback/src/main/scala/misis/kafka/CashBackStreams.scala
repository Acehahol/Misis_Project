package misis.kafka

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{Sink, Source}
import io.circe.generic.auto._
import io.circe.syntax._
import misis.WithKafka
import misis.model.{AccountUpdated, CashBackUpdate, CashBackUpdated}
import misis.repository.CashBackRepository
import org.apache.kafka.clients.producer.ProducerRecord

import scala.concurrent.ExecutionContext

class CashBackStreams(repository: CashBackRepository)(implicit
    val system: ActorSystem,
    executionContext: ExecutionContext
) extends WithKafka {
    override def group: String = "cashback"

    kafkaSource[AccountUpdated]
        .filter(event => event.transaction == 3)
        .map(command =>
            produceCommand(
                CashBackUpdate(
                    accountId = command.accountId,
                    value = command.value,
                    category = command.category
                )
            )
        )
        .to(Sink.ignore)
        .run()
    kafkaSource[CashBackUpdate]
        .filter(command => repository.containsCat(command.category))
        .mapAsync(1) { command =>
            if (repository.containsAccount(command.accountId)) {
                repository
                    .updateAccount(command.accountId, (command.value * repository.getProc(command.category)).toInt)
                    .map(_ =>
                        CashBackUpdated(
                            accountId = command.accountId,
                            valueCb = (command.value * repository.getProc(command.category)).toInt,
                            category = command.category
                        )
                    )
            } else {
                repository.addAccount(command.accountId)
                repository
                    .updateAccount(command.accountId, (command.value * repository.getProc(command.category)).toInt)
                    .map(_ =>
                        CashBackUpdated(
                            accountId = command.accountId,
                            valueCb = (command.value * repository.getProc(command.category)).toInt,
                            category = command.category
                        )
                    )
            }
        }
        .to(kafkaSink)
        .run()
    kafkaSource[CashBackUpdated]
        .map { e =>
            println(
                s"На аккаунт ${e.accountId} начисленно ${e.valueCb} баллов"
            )
            e
        }
        .to(Sink.ignore)
        .run()
}
