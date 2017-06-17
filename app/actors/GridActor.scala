package actors

import actors.GridActor.{CreateGrid, GetGridDetails}
import actors.MeterActor.CreateMeter
import akka.actor.{Actor, Props}
import models.{Grid, MeasValue, Meter}
import play.api.Logger

import scala.collection.mutable.ArrayBuffer

/**
  * Created by abhiso on 5/10/17.
  */
object GridActor {

  def props = Props[GridActor]
  // create the grid
  case class CreateGrid(grid: Grid)

  // get the grid details
  case object GetGridDetails

  // Get All Meters in the Grid
  case object GetMeters
  // Get Grid By Id
}

class GridActor() extends Actor {

  /** placeholder for the grid this actor is working on */
  var grids = ArrayBuffer[Grid]()

  override def receive: Receive = {
    case CreateGrid(grid) =>
      grids += grid
      Logger.info(s"Creating new ${grid}")

      val measures = grid.measures.split(",") map (_.trim) map (_.toLong)
      val defaultValues = grid.initialValues.split(",") map (_.trim) map (_.toDouble)

      // get the meas value pairs for the meter
      val measValues: Seq[MeasValue] = (measures zip defaultValues).toVector map (t => MeasValue(t._1, t._2))
      Logger.info(s"default value $measValues")

      val createdMeters = (0 until grid.numOfMeters)
        .map(i => Meter(i.toLong, s"Meter-$i-${grid.id}", measValues, grid.meterType, grid.frequencyInSec))

      grid.meters.appendAll(createdMeters)

      grid.meters.foreach(meter => {
        val meterActor = context.actorOf(MeterActor.props, s"meter-${meter.id}")
        meterActor ! CreateMeter(meter)
      })

    case GetGridDetails =>
      sender ! grids(0)
  }
}
