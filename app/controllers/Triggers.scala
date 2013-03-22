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

/*case class Trigger(
  id: String,
  fUId: String,
  vId: String,
  text: String
)*/

object Triggers extends Controller with Secured {

  implicit val session = AppDB.database.withSession {
    implicit session: Session => session
  }

  def newCheckIn() = Action { implicit request =>
    println("ok")
    println(request.toString())
    Ok("thx")
  }

}