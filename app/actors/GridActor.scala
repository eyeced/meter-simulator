package actors

import java.time.Instant

import actors.GridActor._
import actors.KafkaProducerActor.SendToTopic
import actors.MeterActor.CreateMeter
import akka.actor.Props
import akka.persistence.{PersistentActor, RecoveryCompleted}
import models.{Grid, MeasValue, Meter}
import play.api.Logger

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by abhiso on 5/10/17.
  */
object GridActor {

  def props(gridId: String, grid: Grid, initialSum: Double) = Props(new GridActor(gridId, grid, initialSum))

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

  case class Add(measValue: MeasValue)

  case class Obj(timestamp: Long, value: Double)

  case object Publish
}

class GridActor(gridId: String, var myGrid: Grid, var sum: Double) extends PersistentActor {

  lazy val kafkaProducerActor = context.actorSelection("/user/grid-generator/kafka-producer")

  implicit val ec: ExecutionContext = context.dispatcher

  override def receiveRecover: Receive = {
    case event: Event => updateState(event)
    case RecoveryCompleted =>
      Logger.info(s"Recovery Complete $myGrid")
  }

  override def receiveCommand: Receive = {
    case CreateGrid(grid) =>

      Logger.info(s"Creating new ${grid}")

//      persist(CreatedGrid(grid))(updateState)

      val measures = grid.measures.split(",") map (_.trim) map (_.toLong)
      val defaultValues = grid.initialValues.split(",") map (_.trim) map (_.toDouble)

      // get the meas value pairs for the meter
      val measValues: Seq[MeasValue] = (measures zip defaultValues).toVector map (t => MeasValue(t._1, t._2))
      Logger.info(s"default value $measValues")

      val createdMeters = (0 until grid.numOfMeters)
        .map(i => Meter(i.toLong, s"Meter-$i-${grid.id}", measValues, grid.meterType, grid.frequencyInSec, grid.id))

      grid.meters.appendAll(createdMeters)

      grid.meters.foreach(meter => {
        val meterActor = context.actorOf(MeterActor.props(meter), s"meter-${meter.id}")
        meterActor ! CreateMeter(meter)
      })

      context.system.scheduler.schedule(0 seconds, 10 seconds, self, Publish)

    case GetGridDetails =>
      sender ! myGrid

    case Add(measValue) =>

      Logger.info(s"Adding to the sum $sum value $measValue from ${sender.toString()}")
      sum = sum + measValue.value
      self ! Publish

    case Publish =>
      kafkaProducerActor ! SendToTopic("grid-data", myGrid.id.toString, Obj(Instant.now().toEpochMilli, sum))

  }

  override def persistenceId: String = gridId

  def updateState: Event => Unit = {
    case CreatedGrid(grid) => myGrid = grid
  }
}
