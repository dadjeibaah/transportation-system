package actors

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, Props}
import akka.stream.ActorMaterializer
import akka.stream.alpakka.amqp.scaladsl._
import akka.stream.alpakka.amqp.{AmqpConnectionDetails, AmqpSinkSettings, ExchangeDeclaration}
import akka.stream.scaladsl.Source
import akka.util.{ByteString, Timeout}
import java.util.UUID._
import play.api.libs.json._

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration


/**
  * Created by dennis on 6/18/17.
  */
class LineMonitorActor extends Actor with ActorLogging {

  import context.dispatcher

  override def preStart(): Unit = {
    implicit val askTimeout = Timeout(5L, TimeUnit.SECONDS)
    implicit val materializer = ActorMaterializer()

    implicit val amqpConnection = AmqpConnectionDetails("localhost", 32771)
    val exchangeName = "transitExchange"
    val exchangeDeclaration = ExchangeDeclaration(exchangeName, "topic", durable = true)
    val routingKey = "transit.line.a"
    val amqpSink = AmqpSink.simple(
      AmqpSinkSettings(amqpConnection)
        .withExchange(exchangeName)
        .withDeclarations(exchangeDeclaration)
        .withRoutingKey(routingKey)
    )
    val jsonString = Json.obj(
      "messageId" -> randomUUID.toString,
      "message" -> Json.obj(
        "lineName" -> "a-line",
        "stationName" -> "Port Royale",
        "busId" -> 100L
      )
    ).toString()
    Source
      .tick[String](FiniteDuration(5, TimeUnit.SECONDS), interval = FiniteDuration(1, TimeUnit.SECONDS), tick = "testMessage")
      .mapAsync(parallelism = 3)(elem => Future[ByteString](ByteString(jsonString))).runWith(amqpSink)
  }

  override def receive: Receive = Actor.emptyBehavior
}

object LineMonitorActor {
  def props: Props = Props(new LineMonitorActor())
}
