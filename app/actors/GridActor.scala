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
}

class GridActor extends Actor {

  var map = mutable.HashMap[Int, Grid]()

  override def receive: Receive = {
    case CreateGrid(grid) =>
      Logger.info(s"Appending grid to ${map}")
      map += (grid.id -> grid)

    case GetGridById(id) =>
      Logger.info("Get Grid by id")
      sender ! map.get(id)

    case GetGrids =>
      Logger.info(s"Sending grids ${map}")
      sender ! map.values.toSeq

  }
}
