package controllers

import models._
import play.api._
import play.api.mvc._
import java.util.UUID
import play.api.libs.json._
import slick.session.Session
import play.api.data._
import play.api.data.Forms._
import org.joda.time.DateTime
import java.sql.Timestamp
import play.api.libs.ws.WS
import scala.concurrent._
import ExecutionContext.Implicits.global

import views._

case class CheckinData(
  secret: String,
  user: String,
  checkin: String
)

object Triggers extends Controller with Secured {

  implicit val session = AppDB.database.withSession {
    implicit session: Session => session
  }

  def newCheckIn() = Action { implicit request =>
    println("ok")
    userForm.bindFromRequest.fold(
      errors => {
        println(errors)
        BadRequest
      },
      form => {
        println(form.secret)
        println(form.user)
        println(form.checkin)
        Ok("thx")
      }
    )
  }

  def userForm = Form(
    mapping(
      "secret" -> text,
      "user" -> text,
      "checkin" -> text
    )(CheckinData.apply)(CheckinData.unapply)
  )

}