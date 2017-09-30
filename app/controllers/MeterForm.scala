package controllers

import models.{MeasValue, Meter}
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._

/**
  * Created by abhiso on 5/29/17.
  */
object MeterForm {

val meterForm = Form(
    mapping(
      "id"              -> longNumber,
      "name"            -> text,
      "measValues"      -> seq(
        mapping(
          "measId"      -> longNumber,
          "value"       -> of(doubleFormat)
        )(MeasValue.apply)(MeasValue.unapply)
      ),
      "meterType"       -> text,
      "frequencyInSec"  -> number,
      "gridId"          -> longNumber
    )(Meter.apply)(Meter.unapply)
  )}
