package actors

import java.time.Instant

import actors.KafkaProducerActor.Send
import actors.MeterActor._
import akka.actor.Props
import akka.persistence.{PersistentActor, RecoveryCompleted}
import com.emeter.a2f.kafka.message.A2FKafkaMessage
import com.emeter.cdci.data.message.{AssetDataPoint, DataPoint, MeasDataPoint}
import models.{MeasValue, Meter}
import play.api.Logger

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by abhiso on 5/10/17.
  */
object MeterActor {
  def props(meter: Meter) = Props(new MeterActor(meter))

  sealed trait Event
  sealed trait Command
  // create a meter with defined measurements and it's starting value, and it's type
  case class CreateMeter(meter: Meter) extends Command
  case class CreatedMeter(meter: Meter) extends Event
  // get the current values of the meter for all measurements
  case object GetCurrentValues extends Command
  // get the current value for the measurement
  case class GetCurrentValue(meas: String)
  // set the value for the meas
  case class SetValue(measValue: MeasValue) extends Command
  case class ValueChanged(measValue: MeasValue) extends Event
  // get all measurements for the meter
  case object GetMeasurements extends Command
  // publish all values to kafka
  case object Publish extends Command
}
class MeterActor(var myMeter: Meter) extends PersistentActor {

  lazy val kafkaProducerActor = context.actorSelection("/user/grid-generator/kafka-producer")

  implicit val ec: ExecutionContext = context.dispatcher

  override def receiveRecover: Receive = {
    case event: Event => updateState(event)
    case RecoveryCompleted => Logger.info(s"Recovery Completed ${myMeter.name}")
  }

  override def persistenceId: String = myMeter.name

  override def receiveCommand: Receive = {
    case CreateMeter(meter) => persist(CreatedMeter(meter))(updateState)

    case GetCurrentValues =>
      sender ! myMeter

    case SetValue(value) => persist(ValueChanged(value))(updateState)

    case Publish =>
      val msg = getMessage(myMeter)
      Logger.info(s"Sending message $msg")
      kafkaProducerActor ! Send(myMeter.id.toString, msg)
  }


  def updateState: Event => Unit = {
    case CreatedMeter(meter) =>
      Logger.info(s"Creating meter ${meter.name}")
      Logger.info(s"${self.path}")
      myMeter = meter
      val start = meter.frequencyInSec - (Instant.now().getEpochSecond % meter.frequencyInSec)
      context.system.scheduler.schedule(start seconds, meter.frequencyInSec seconds, self, Publish)

    case ValueChanged(value) =>
      myMeter.measValues.find(mv => mv.measId == value.measId) match {
        case None =>
          Logger.info("Could not find any value adding to the list")
          myMeter.measValues = value :: myMeter.measValues.toList
        case Some(measValue) =>
          Logger.info("found the value editing replacing it with new value")
          myMeter.measValues = value :: myMeter.measValues.filter(mv => mv.measId != measValue.measId).toList
      }
  }

  /**
    * get the message for the meter
    * @param meter
    * @return a2f kafka message
    */
  def getMessage(meter: Meter): A2FKafkaMessage = {
    val epochSec = Instant.now().getEpochSecond - (Instant.now().getEpochSecond % meter.frequencyInSec)

    val adp = getAssetDataPoint(meter.id, Instant.ofEpochSecond(epochSec), meter)
    new A2FKafkaMessage(java.lang.Long.valueOf(1), adp)
  }

  /**
    * get asset data point
    * @param svcPtId for svc pt
    * @param instant for instant
    * @return asset data point
    */
  def getAssetDataPoint(svcPtId: java.lang.Long, instant: Instant, meter: Meter): AssetDataPoint = {
    val adp = new AssetDataPoint()
    adp.setAssetId(svcPtId)
    adp.setAssetType("SVC_PT")

    val list = new java.util.ArrayList[MeasDataPoint]()

    meter.measValues.toList.foreach(measValue => {
      val mdp = new MeasDataPoint()
      mdp.setMeasTypeId(measValue.measId)

      val dataPoints = new java.util.ArrayList[DataPoint]()
      val dp = new DataPoint()
      dp.setReadTime(java.util.Date.from(instant))
      dp.setValue(measValue.value)
      dp.setFlag(java.lang.Long.valueOf(1))
      dataPoints.add(dp)
      mdp.setDataPoints(dataPoints)
      list.add(mdp)
    })

    adp.setReads(list)
    adp
  }
}
