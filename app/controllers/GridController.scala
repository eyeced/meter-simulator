package controllers

import javax.inject.Inject

import controllers.GridForm._
import models.Grid
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by abhiso on 5/10/17.
  */
class GridController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport  {

  private val grids = ArrayBuffer(Grid("Grid1"), Grid("Grid2"))

  private val postUrl = routes.GridController.createGrid

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def listGrids = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.grid(grids, gridForm, postUrl))
  }

  def createGrid = Action { implicit request =>
    val errorFunction = { formWithErrors: Form[Grid] =>
      // This is the bad case, where the form had validation errors.
      // Let's show the user the form again, with the errors highlighted.
      // Note how we pass the form with errors to the template.
      BadRequest(views.html.grid(grids, formWithErrors, postUrl))
    }

    val successFunction = { grid: Grid =>
      // This is the good case, where the form was successfully parsed as a Widget.
      grids.append(grid)
      Redirect(routes.GridController.listGrids())
    }

    val formValidationResult = gridForm.bindFromRequest
    formValidationResult.fold(errorFunction, successFunction)
  }

}
