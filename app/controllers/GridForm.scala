package controllers

import models.Grid
import play.api.data.Form
import play.api.data.Forms._

/**
  * Created by abhiso on 5/10/17.
  */
object GridForm {

  val gridForm = Form(
    mapping(
      "id"   -> number,
      "name" -> text
    )(Grid.apply)(Grid.unapply)
  )
}
