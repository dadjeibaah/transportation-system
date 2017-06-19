package actors

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, Props}
import akka.stream.alpakka.amqp.scaladsl.AmqpSource
import akka.stream.alpakka.amqp._
import com.google.inject.Inject
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.util._


/**
  * Created by dennis on 6/18/17.
  */
class StationMonitorActor extends Actor with ActorLogging {
  override def preStart(): Unit = {
    implicit val askTimeout = Timeout(5L,TimeUnit.SECONDS)
    implicit val materializer = ActorMaterializer()

    implicit val amqpConnection = AmqpConnectionDetails("localhost", 32771)
    val queueName = "transit.line.a"
    val routingKey = "transit.line.*"
    val queueDeclaration = BindingDeclaration(queueName, routingKey = Some("transit.line.#"),exchange = "transitExchange")
    val amqpSource = AmqpSource(NamedQueueSourceSettings(amqpConnection, queueName)
      .withDeclarations(queueDeclaration), bufferSize = 1)
    amqpSource.mapAsync(parallelism = 1)(msg => context.self ? msg).runWith(Sink.ignore)
  }

  override def receive: Receive = {
    case _ =>
      log.error("received message")
      sender() ! "reply"
  }
}

object StationMonitorActor{

  def props:Props = Props(new StationMonitorActor())
}
