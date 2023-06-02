package misis.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.TopicName
import misis.kafka.CashBackStreams
import misis.model.{AccountUpdate, Category}
import misis.repository.CashBackRepository

import scala.concurrent.ExecutionContext

class Route(repository: CashBackRepository, streams: CashBackStreams)(implicit ec: ExecutionContext)
    extends FailFastCirceSupport {
    implicit val commandTopicName: TopicName[AccountUpdate] = streams.simpleTopicName[AccountUpdate]
    def routes =
        (path("cashback" / IntNumber) & get) { (accountId) =>
            val amount = repository.getCbBalance(accountId)
            complete(s"На ${accountId} аккаунту ${amount} баллов")
        } ~
            (path("getcashback" / IntNumber)) { (accountId) =>
                val amount = repository.getCbBalance(accountId)
                streams.produceCommand(AccountUpdate(accountId, amount, transaction = 5))
                repository.updateAccount(accountId, -amount)
                complete(s"На аккаунт ${accountId} начисленно ${amount}")
            } ~
            (path("addcategory") & post & entity(as[Category])) { Category =>
                repository.addCategory(Category)
                complete(Category)
            }
}
