package models


case class Grid(id: Int = 0, name: String, meterType: String, measures: String, numOfMeters: Int)

case class Meter(id: Long, name: String, measValues: Vector[(String, Double)], meterType: String)


