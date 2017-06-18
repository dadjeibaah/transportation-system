import actors.StationMonitorActor
import akka.actor.{ActorSystem, Props}
import com.google.inject.{AbstractModule, Inject, Singleton}
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
class Startup @Inject()(system: ActorSystem){
  println("THIS PRINTED AT STARTUP")
  val monitorActor = system.actorOf(Props[StationMonitorActor],"master-monitor")
  monitorActor ! "test message"
}
