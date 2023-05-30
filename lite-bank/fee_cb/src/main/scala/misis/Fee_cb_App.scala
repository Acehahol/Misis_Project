package misis

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import misis.kafka.Fee_cb_streams
import misis.model.AccountUpdate
import misis.repository.{CashBackRepository}
import misis.route.Route
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

object Fee_cb_App extends App {
    implicit val system: ActorSystem = ActorSystem("MyApp")
    implicit val ec = system.dispatcher
    val port = ConfigFactory.load().getInt("port")

    private val repository = new CashBackRepository()
    private val streams = new Fee_cb_streams(repository)

    private val route = new Route(repository,streams)
    Http().newServerAt("0.0.0.0", port).bind(route.routes)
}
