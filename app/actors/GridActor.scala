package actors

import akka.actor.Actor

/**
  * Created by abhiso on 5/10/17.
  */
object GridActor {
  // create the grid
  case class CreateGrid(id: Long, udcId: String)
  // Get All Meters in the Grid
  case object GetMeters
  // Add Meters to the grid
  case class AddMeters(numOfMeters: Long, measurements: Vector[String], meterType: String)
}

class GridActor extends Actor {
  override def receive: Receive = ???
}
