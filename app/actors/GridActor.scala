package actors

import actors.GridActor.{CreateGrid, CreatedGrid, Event, GetGridDetails}
import actors.MeterActor.CreateMeter
import akka.actor.{Actor, Props}
import akka.persistence.{PersistentActor, RecoveryCompleted}
import models.{Grid, MeasValue, Meter}
import play.api.Logger

import scala.collection.mutable.ArrayBuffer

/**
  * Created by abhiso on 5/10/17.
  */
object GridActor {

  def props(gridId: String, grid: Grid) = Props(new GridActor(gridId, grid))

  sealed trait Command
  // create the grid
  case class CreateGrid(grid: Grid) extends Command

  sealed trait Event
  case class CreatedGrid(grid: Grid) extends Event

  // get the grid details
  case object GetGridDetails

  // Get All Meters in the Grid
  case object GetMeters
  // Get Grid By Id
}

class GridActor(gridId: String, var myGrid: Grid) extends PersistentActor {

  override def receiveRecover: Receive = {
    case event: Event => updateState(event)
    case RecoveryCompleted =>
      Logger.info(s"Recovery Complete $myGrid")
  }

  override def receiveCommand: Receive = {
    case CreateGrid(grid) =>

      Logger.info(s"Creating new ${grid}")

      persist(CreatedGrid(grid))(updateState)

      val measures = grid.measures.split(",") map (_.trim) map (_.toLong)
      val defaultValues = grid.initialValues.split(",") map (_.trim) map (_.toDouble)

      // get the meas value pairs for the meter
      val measValues: Seq[MeasValue] = (measures zip defaultValues).toVector map (t => MeasValue(t._1, t._2))
      Logger.info(s"default value $measValues")

      val createdMeters = (0 until grid.numOfMeters)
        .map(i => Meter(i.toLong, s"Meter-$i-${grid.id}", measValues, grid.meterType, grid.frequencyInSec))

      grid.meters.appendAll(createdMeters)

      grid.meters.foreach(meter => {
        val meterActor = context.actorOf(MeterActor.props(meter), s"meter-${meter.id}")
        meterActor ! CreateMeter(meter)
      })

    case GetGridDetails =>
      sender ! myGrid
  }

  override def persistenceId: String = gridId

  def updateState: Event => Unit = {
    case CreatedGrid(grid) => myGrid = grid
  }
}
