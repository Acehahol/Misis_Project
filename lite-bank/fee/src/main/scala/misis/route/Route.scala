package misis.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.TopicName
import misis.kafka.FeeStreams
import misis.model.AccountUpdate
import misis.repository.FeeRepository

import scala.concurrent.ExecutionContext

class Route(repository: FeeRepository, streams: FeeStreams)(implicit ec: ExecutionContext)
    extends FailFastCirceSupport {
    implicit val commandTopicName: TopicName[AccountUpdate] = streams.simpleTopicName[AccountUpdate]
    def routes =
        (path("fee_limit" / IntNumber) & get) { (accountId) =>
            val amount = repository.getLmBalance(accountId)
            complete(s"Лимит переводом ${accountId} аккаунте равен ${amount}")
        }
   //         (path("getcashback" / IntNumber)) { (accountId) =>
   //             val amount = repository.getCbBalance(accountId)
    //            streams.produceCommand(AccountUpdate(accountId, amount, transaction = 5))
    //            repository.updateAccount(accountId, -amount)
     //           amount
      //          complete(s"На аккаунт ${accountId} начисленно ${amount}")
      //      }
}
