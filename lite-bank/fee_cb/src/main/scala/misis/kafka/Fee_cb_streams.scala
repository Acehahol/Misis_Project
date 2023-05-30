package misis.kafka

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{Sink, Source}
import io.circe.generic.auto._
import io.circe.syntax._
import misis.WithKafka
import misis.model.{AccountUpdate, AccountUpdated, CashBackUpdate, CashBackUpdated}
import misis.repository.CashBackRepository
import org.apache.kafka.clients.producer.ProducerRecord

import scala.concurrent.ExecutionContext

class Fee_cb_streams(repository: CashBackRepository)(implicit
    val system: ActorSystem,
    executionContext: ExecutionContext
) extends WithKafka {
    override def group: String = "fee_cb"

    kafkaSource[AccountUpdated]
        .filter(event => event.transaction == 3)
        .map { command =>
            produceCommand(AccountUpdate(command.accountId, (-command.value * 0.1).toInt, 4))
            println(
                s"С ${command.accountId} счета успешно снята комиссия ${(-command.value * 0.1).toInt}"
            )
            produceCommand(CashBackUpdate(command.accountId, ((command.value * 0.1) / 2).toInt))
        }
        .to(Sink.ignore)
        .run()

    kafkaSource[CashBackUpdate]
        .mapAsync(1) { command =>
            if (repository.containsAccount(command.accountId)) {
                repository
                    .updateAccount(command.accountId, command.value)
                    .map(_ =>
                        CashBackUpdated(
                            accountId = command.accountId,
                            value = command.value
                        )
                    )
            } else {
                repository
                    .addAccount(command.accountId)
                repository
                    .updateAccount(command.accountId, command.value)
                    .map(_ =>
                        CashBackUpdated(
                            accountId = command.accountId,
                            value = command.value
                        )
                    )
            }
        }
        .to(kafkaSink)
        .run()
    kafkaSource[CashBackUpdated]
        .map { e =>
            println(
                s"На аккаунт ${e.accountId} начислено ${e.value} cashback"
            )
            e
        }
        .to(Sink.ignore)
        .run()
}
