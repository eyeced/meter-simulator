package actors

import actors.MeterActor.CreateMeter
import akka.actor.Actor
import models.Meter

import scala.collection.mutable.ArrayBuffer

/**
  * Created by abhiso on 5/10/17.
  */
object MeterActor {
  // create a meter with defined measurements and it's starting value, and it's type
  case class CreateMeter(id: Long, udcId: String, measValues: Vector[(String, Double)], meterType: String)
  // get the current values of the meter for all measurements
  case object GetCurrentValues
  // get the current value for the measurement
  case class GetCurrentValue(meas: String)
  // set the value for the meas
  case class SetValue(meas: String, newValue: Double)
  // get all measurements for the meter
  case object GetMeasurements
  // publish all values to kafka
  case object Publish
}
class MeterActor extends Actor {

  var meters = ArrayBuffer[Meter]()

  override def receive: Receive = {
    case CreateMeter(id, udcId, measValues, meterType) =>
      meters.append(Meter(id, udcId, measValues, meterType))

  }
}
