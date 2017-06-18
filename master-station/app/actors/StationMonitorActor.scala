package actors

import akka.actor.{Actor, ActorLogging}


/**
  * Created by dennis on 6/18/17.
  */
class StationMonitorActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case _ => log.error("message received")
  }
}
