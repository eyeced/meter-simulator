package controllers

import javax.inject.Inject

import actors.MeterActor
import actors.MeterActor.{GetCurrentValues, SetValue}
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.stream.scaladsl.{Flow, Sink}
import akka.util.Timeout
import controllers.MeasValueForm.measValueForm
import models.{MeasValue, Meter}
import play.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json._
import play.api.mvc.{Action, Controller, WebSocket}
import play.api.libs.functional.syntax._
import services.Kafka

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by abhiso on 5/29/17.
  */
class MeterController @Inject()(val messagesApi: MessagesApi, system: ActorSystem, kafka: Kafka)
                                (implicit exec: ExecutionContext) extends Controller with I18nSupport {

  implicit val timeout: Timeout = 5.seconds

  implicit val measValueWrites = new Writes[MeasValue] {
    override def writes(o: MeasValue): JsValue = Json.obj(
      "measId" -> o.measId,
      "value"  -> o.value
    )
  }

  implicit val measValueReads: Reads[MeasValue] = (
      (JsPath \ "measId").read[Long] and
      (JsPath \ "value").read[Double]
    )(MeasValue.apply _)

  implicit val meterWrites = new Writes[Meter] {
    override def writes(o: Meter): JsValue = Json.obj(
      "id"    -> o.id,
      "name"  -> o.name,
      "meterType" -> o.meterType,
      "measValues" -> Json.arr(
        o.measValues map (Json.toJson(_))
      ),
      "freqInSec" -> o.frequencyInSec
    )
  }

  /**
    * get the meter detail
    * @param gridId the grid id
    * @param meterId the meter id
    * @return result
    */
  def detail(gridId: Int, meterId: Long) = Action.async { implicit request =>
    val meterActorPath: String = s"/user/grid-generator/grid-$gridId/meter-$meterId"
    val meterActor = system.actorSelection(meterActorPath)
    (meterActor ? GetCurrentValues).mapTo[Meter].map { meter =>
      Ok(views.html.meterDetail(measValueForm, meter, gridId))
    }
  }

  /**
    * get the meter detail in json
    * @param gridId the grid id
    * @param meterId the meter id
    * @return result
    */
  def index(gridId: Int, meterId: Long) = Action.async { implicit request =>
    val meterActorPath: String = s"/user/grid-generator/grid-$gridId/meter-$meterId"
    val meterActor = system.actorSelection(meterActorPath)

    (meterActor ? GetCurrentValues).mapTo[Meter].map { meter =>
      Ok(Json.toJson(meter))
    }
  }

  /**
    * edit the meas value for the meter and grid
    * @param gridId the grid id
    * @param meterId the meter id
    * @return json response
    */
  def editMeasValue(gridId: Int, meterId: Long) = Action { implicit request =>
    val meterActorPath: String = s"/user/grid-generator/grid-$gridId/meter-$meterId"
    val meterActor = system.actorSelection(meterActorPath)
    Logger.info("edit meas value call made")

    request.body.asJson.map { json =>
      Logger.info(s"edit meas value call made $json")
      Logger.info(s"${Json.fromJson[MeasValue](json)}")
      val measValue: MeasValue = Json.fromJson[MeasValue](json).get
      Logger.info(s"new value $measValue")
      meterActor ! SetValue(measValue)
      Ok(Json.toJson(measValue))

    }.getOrElse {
      BadRequest("Expecting Json")
    }
  }

  /**
    * meter edit function
    * @param gridId the grid id
    * @param meterId the meter id
    * @return action async result
    */
  def edit(gridId: Int, meterId: Long) = Action.async { implicit request =>
    val errorFunction = { formWithErrors: Form[MeasValue] =>
      val meterActorPath: String = s"/user/grid-generator/grid-$gridId/meter-$meterId"
      val meterActor = system.actorSelection(meterActorPath)
      // This is the bad case, where the form had validation errors.
      // Let's show the user the form again, with the errors highlighted.
      // Note how we pass the form with errors to the template.
      Logger.info("Bad request" + formWithErrors.errors)

      (meterActor ? GetCurrentValues).mapTo[Meter].map { meter =>
        BadRequest(views.html.meterDetail(formWithErrors, meter, gridId))
      }
    }

    val successFunction = { measValue: MeasValue =>
      val meterActorPath: String = s"/user/grid-generator/grid-$gridId/meter-$meterId"
      val meterActor = system.actorSelection(meterActorPath)
      // This is the good case, where the form was successfully parsed as a Widget.
      meterActor ! MeterActor.SetValue(measValue)
      Logger.info("Setting New Value")
      (meterActor ? GetCurrentValues).mapTo[Meter].map { meter =>
        Ok(views.html.meterDetail(measValueForm, meter, gridId))
      }
    }

    val formValidationResult = measValueForm.bindFromRequest
    formValidationResult.fold(errorFunction, successFunction)
  }

  /**
    * @return the kafka web socket which reads data from topic meter data
    */
  def kafkaWs(key: Int, topic: String) = WebSocket.acceptOrResult[Any, String] { _ =>
    kafka.source(topic) match {
      case Failure(e) =>
        Future.successful(Left(InternalServerError("Could not connect to Kafka")))
      case Success(source) =>
        val flow = Flow.fromSinkAndSource(Sink.ignore, source.filter(_.key.toInt == key).map(_.value))
        Future.successful(Right(flow))
    }
  }

  def chart(gridId: Int) = Action {
    Ok(views.html.charts(gridId, "Grid Aggregated Data", s"Aggregate Data For Grid $gridId", s"chart/$gridId/kafka/grid-data"))
  }

  def meterChart(meterId: Int) = Action {
    Ok(views.html.charts(meterId, "Meter Data", s"Data For Meter $meterId", s"chart/$meterId/kafka/meter-data"))
  }

}
