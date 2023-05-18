package misis.kafka

import akka.actor.ActorSystem
import misis.WithKafka
import misis.

import scala.concurrent.ExecutionContext

class Streams()(implicit val system: ActorSystem, executionContext: ExecutionContext) extends WithKafka {
    override def group: String = "operation"

    kafkaSource[TransferResponse]
        .filter(response => response.transactionId == transactionId)
        .map { response =>
            if (response.isSuccess) {
                // сохраняем данные о новом переводе в локальное хранилище
                val newTransfer = Transfer(transactionId, sender, recipient, amount)
                // ...
            } else {
                // произошла ошибка, откатываем транзакцию
                // ...
            }
            response
        }
        .to(Sink.ignore)
        .run()

}
