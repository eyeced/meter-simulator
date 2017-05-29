package models


case class Grid(id: Int = 0, name: String, meterType: String,
                measures: String, numOfMeters: Int, frequencyInSec: Int,
                initialValues: String)

case class Meter(id: Long, name: String, measValues: Vector[(Long, Double)],
                 meterType: String, frequencyInSec: Int)


