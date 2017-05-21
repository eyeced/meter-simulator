package actors

import actors.GridGeneratorActor.{AddMeters, CreateGrid}
import akka.actor.{Actor, ActorRef, Props}
import models.Grid

/**
  * Actor that handles the meter creation
  * Created by abhiso on 5/20/17.
  */
object GridGeneratorActor {
  def props = Props[GridGeneratorActor]
  // Add Meters to the grid
  case class AddMeters(numOfMeters: Int, measurements: Vector[String], meterType: String)

  case class CreateGrid(grid: Grid)
}
class GridGeneratorActor extends Actor {
  var counter = 0
  override def receive: Receive = {
    case CreateGrid(grid) =>
      counter += 1
      val gridActor = context.actorOf(GridActor.props, s"grid-actor-${counter}")
      gridActor ! Grid(counter, grid.name, grid.meterType, grid.measures, grid.numOfMeters)
  }
}
