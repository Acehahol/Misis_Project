package misis.kafka

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import io.circe.generic.auto._
import misis.WithKafka
import misis.model.{AccountUpdate, AccountUpdated, AccountCreate, AccountCreated}
import misis.repository.AccountRepository

import scala.concurrent.ExecutionContext

class AccountStreams(repository: AccountRepository)(implicit
    val system: ActorSystem,
    executionContext: ExecutionContext
) extends WithKafka {

    def group = s"accounts"

    kafkaSource[AccountUpdate]
        .filter(command =>
            repository.containsAccount(command.accountId) && repository.getAccountBalance(
                command.accountId
            ) + command.value >= 0
        )
        .mapAsync(1) { command =>
            repository
                .updateAccount(command.accountId, command.value)
                .map(_ =>
                    AccountUpdated(
                        accountId = command.accountId,
                        value = command.value,
                        transaction = command.transaction,
                        directId = command.directId
                    )
                )
        }
        .to(kafkaSink)
        .run()

    kafkaSource[AccountUpdated]
        .map { e =>
            if (e.transaction != 3) {
                println(
                    s"Аккаунт ${e.accountId} обновлен на сумму ${e.value}. Баланс: ${repository
                            .getAccountBalance(e.accountId)}. Операция ${e.transaction}"
                )
            } else {
                println(
                    s"С  ${e.accountId} аккаунта переведено ${e.value} на ${e.directId} аккаунт Баланс: ${repository
                            .getAccountBalance(e.accountId)}"
                )
            }
            e
        }
        .to(Sink.ignore)
        .run()
    kafkaSource[AccountCreate]
        .mapAsync(1) { command =>
            repository
                .addAccount(command.accountId)
                .map(_ =>
                    AccountCreated(
                        accountId = command.accountId
                    )
                )
        }
        .to(kafkaSink)
        .run()

    kafkaSource[AccountCreated]
        .map { e =>
            println(
                s"Аккаунт ${e.accountId} cоздан"
            )
            e
        }
        .to(Sink.ignore)
        .run()

}
