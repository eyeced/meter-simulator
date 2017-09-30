package actors

import akka.actor.{Actor, Props}
import akka.stream.scaladsl.{Flow, Sink}
import services.Kafka

import scala.util.{Failure, Success}

object PivotActor {
  def props(kafka: Kafka) = Props(new PivotActor(kafka))
}

class PivotActor(kafka: Kafka) extends Actor {
  override def receive = ???

  def consume(topic: String): Unit = {
    kafka.source(topic) match {
      case Failure(e) => throw new RuntimeException(e)
      case Success(source) =>
        val flow = Flow.fromSinkAndSource(Sink.ignore, source)
    }
  }
}
