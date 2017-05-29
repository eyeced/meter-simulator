package controllers

import javax.inject.Inject

import actors.GridGeneratorActor
import actors.GridGeneratorActor.{GetGridById, GetGrids}
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import controllers.GridForm._
import models.Grid
import play.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by abhiso on 5/10/17.
  */
class GridController @Inject()(val messagesApi: MessagesApi, system: ActorSystem)(implicit exec: ExecutionContext) extends Controller with I18nSupport  {

  implicit val timeout: Timeout = 5.seconds

  private val postUrl = routes.GridController.createGrid

  val gridGenActor = system.actorOf(GridGeneratorActor.props, "grid-generator")

  // default page for the grid controller
  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  // Get the grid detail for the given id
  def detail(id: Int) = Action.async { implicit request =>
    (gridGenActor ? GetGridById(id)).mapTo[Option[Grid]].map {
      case Some(g) =>
        Ok(views.html.gridDetail(g))
      case None =>
        Redirect(routes.GridController.listGrids())
    }
  }

  // list all grids
  def listGrids = Action.async { implicit request =>
    (gridGenActor ? GetGrids).mapTo[Seq[Grid]].map { implicit gridSeq =>
      Ok(views.html.grid(gridSeq))
    }
  }

  def create = Action { implicit request =>
    Ok(views.html.gridCreate(gridForm, postUrl))
  }

  // create a new grid
  def createGrid = Action { implicit request =>
    val errorFunction = { formWithErrors: Form[Grid] =>
      // This is the bad case, where the form had validation errors.
      // Let's show the user the form again, with the errors highlighted.
      // Note how we pass the form with errors to the template.
      Logger.info("Bad request" + formWithErrors.errors)
      BadRequest(views.html.gridCreate(formWithErrors, postUrl))
    }

    val successFunction = { grid: Grid =>
      // This is the good case, where the form was successfully parsed as a Widget.
      gridGenActor ! GridGeneratorActor.CreateGrid(grid)
      Logger.info("Creating grid")
      Redirect(routes.GridController.listGrids())
    }

    val formValidationResult = gridForm.bindFromRequest
    formValidationResult.fold(errorFunction, successFunction)
  }

}
