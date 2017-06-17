package controllers

import models.MeasValue
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._

/**
  * Created by abhiso on 5/29/17.
  */
object MeasValueForm {

  val measValueForm = Form(
    mapping(
      "measId"      -> longNumber,
      "value"       -> of(doubleFormat)
    )(MeasValue.apply)(MeasValue.unapply)
  )
}
