package actors

import actors.KafkaConsumerActor.Poll
import akka.actor.{Actor, Props}
import com.emeter.cdci.data.message.AssetDataPoint
import play.api.Configuration

object KafkaConsumerActor {
  def props(config: Configuration) = Props(new KafkaConsumerActor(config))

  case class Poll(topic: String)
}
class KafkaConsumerActor(configuration: Configuration) extends Actor {
  override def receive = {
    case Poll(topic) =>
  }

  def poll(topic: String) = ???

  /**
    * when the asset data point is consumed
    * from the message we would extract the grid id for that message.
    *
    * And then send out a message to the grid actor where we are storing the sum of all values in
    * that grid for a meas type.
    *
    * In the grid actor when this message is received by it, it would compare with the latest cached value for that
    * meter in that grid
    *
    */

}
