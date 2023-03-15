package main.repository

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import main.model.{Account, Transaction}

import scala.concurrent.{ExecutionContext, Future}

class TranferClient(implicit val ec: ExecutionContext, actorSystem: ActorSystem) extends FailFastCirceSupport {
    def deposit_other(client: Transaction): Future[Either[String, Account]] = {
        val port = ConfigFactory.load().getInt("portout")
        val request = HttpRequest(
            method = HttpMethods.PUT,
            uri = s"http://localhost:" + port + "/account/" + client.id + "/deposit",
            entity = HttpEntity(MediaTypes.`application/json`, client.amount.asJson.noSpaces)
        )
        val future = for {
            response <- Http().singleRequest(request)
            result <- Unmarshal(response)
                .to[Account]
                .map(res => Right(res))
                .recoverWith { case _ =>
                    Unmarshal(response).to[String].map(Left(_))
                }
        } yield result

        future.recover(e => Left(e.getMessage))
    }

}
