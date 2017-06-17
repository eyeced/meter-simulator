package models

import scala.collection.mutable.ArrayBuffer


case class Grid(id: Int = 0, name: String, meterType: String,
                measures: String, numOfMeters: Int, frequencyInSec: Int,
                initialValues: String) {
  var meters: ArrayBuffer[Meter] = ArrayBuffer.empty
}

case class Meter(id: Long, name: String, var measValues: Seq[MeasValue],
                 meterType: String, frequencyInSec: Int)

case class MeasValue(measId: Long, var value: Double)



