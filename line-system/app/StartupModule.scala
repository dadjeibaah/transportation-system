import actors.LineMonitorActor
import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.alpakka.amqp.{AmqpConnectionDetails, AmqpConnectionUri}
import com.google.inject.{AbstractModule, Inject, Singleton}
import play.api.Configuration
import play.api.libs.concurrent.AkkaGuiceSupport

/**
  * Created by dennis on 6/17/17.
  */
class StartupModule extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bind(classOf[Startup]).asEagerSingleton()
  }
}

@Singleton
class Startup @Inject()(system: ActorSystem, configuration:Configuration){
  val monitorActor = system.actorOf(Props[LineMonitorActor],"master-monitor")

  monitorActor ! "test message"
}
