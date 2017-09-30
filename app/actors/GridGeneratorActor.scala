package actors

import actors.GridGeneratorActor.{AddMeters, CreateGrid, GetGridById, GetGrids}
import actors.KafkaProducerActor.PrintProps
import akka.actor.{Actor, ActorRef, Props}
import models.Grid
import play.api.Logger

import scala.collection.mutable

/**
  * Actor that handles the meter creation
  * Created by abhiso on 5/20/17.
  */
object GridGeneratorActor {
  def props = Props[GridGeneratorActor]
  // Add Meters to the grid
  case class AddMeters(numOfMeters: Int, measurements: Vector[String], meterType: String)

  case class CreateGrid(grid: Grid)
  // Get All Grids
  case object GetGrids

  // Get Grid By Id
  case class GetGridById(id: Int)
}

class GridGeneratorActor extends Actor {
  var counter = 0

  var map = mutable.HashMap[Int, Grid]()
  var measCounter = 0

  val kafkaProducerActor = context.actorOf(KafkaProducerActor.props, "kafka-producer")

  override def receive: Receive = {

    case CreateGrid(grid) =>
      Logger.info("Creating grid from generator")
      Logger.info(s"Path - ${self.path.name}")

      counter = counter + 1

      Logger.info(s"grid id ${counter}")

      val newGrid = Grid(counter, grid.name, grid.meterType, grid.measures, grid.numOfMeters, grid.frequencyInSec, grid.initialValues)
      val initialSum = grid.initialValues.toDouble * grid.numOfMeters
      val gridActor = context.actorOf(GridActor.props(s"grid-${counter}", newGrid, initialSum), s"grid-${counter}")
      Logger.info(s"Created grid actor $gridActor")

      gridActor ! GridActor.CreateGrid(newGrid)

      Logger.info(s"map $map before adding new")
      map += (newGrid.id -> newGrid)


    case GetGrids =>
      Logger.info(s"Sending grids ${map}")
      kafkaProducerActor ! PrintProps
      sender ! map.values.toSeq

    case GetGridById(id) =>
      Logger.info("Get Grid by id")
      sender ! map.get(id)
  }
}
