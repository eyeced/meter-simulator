package actors

import actors.GridActor.{CreateGrid, GetGridById, GetGrids}
import akka.actor.{Actor, Props}
import models.Grid
import play.api.Logger

import scala.collection.mutable

/**
  * Created by abhiso on 5/10/17.
  */
object GridActor {

  def props = Props[GridActor]
  // create the grid
  case class CreateGrid(grid: Grid)
  // Get All Meters in the Grid
  case object GetMeters
  // Get All Grids
  case object GetGrids
  // Get Grid By Id
  case class GetGridById(id: Int)
  // Add Meters to the grid
  case class AddMeters(numOfMeters: Long, measurements: Vector[String], meterType: String)
}

class GridActor extends Actor {

  var map = mutable.HashMap[Int, Grid](1 -> Grid(1, "Grid1"), 2 -> Grid(2, "Grid2"))

  private var counter = 2

  override def receive: Receive = {
    case CreateGrid(grid) =>
      Logger.info(s"Appending grid to ${map}")
      counter += 1
      map += (counter -> Grid(counter, grid.name))

    case GetGridById(id) =>
      Logger.info("Get Grid by id")
      sender ! map.get(id)

    case GetGrids =>
      Logger.info(s"Sending grids ${map}")
      sender ! map.values.toSeq
  }
}
