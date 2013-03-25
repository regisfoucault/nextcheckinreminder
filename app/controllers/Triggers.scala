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

import com.typesafe.plugin._
import play.api.Play.current

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
    userForm.bindFromRequest.fold(
      errors => BadRequest,
      form => {
        val secret = form.secret
        val user = form.user
        val checkin = form.checkin
        (for {
          userId <- (Json.parse(checkin) \ "user" \ "id").asOpt[String]
          venueId <- (Json.parse(checkin) \ "venue" \ "id").asOpt[String]
          email <- (Json.parse(checkin) \ "user" \ "contact" \ "email").asOpt[String]
        } yield {
          AppDB.dal.Triggers.doLaunch(userId, venueId) map { trigger =>
            println(trigger)
            val mail = use[MailerPlugin].email
            mail.setSubject("NextCheckInReminder")
            mail.addRecipient("regisfoucault@gmail.com")
            mail.addFrom("Régis from NextCheckInReminder <regis@nextcheckinreminder.mailgun.org>")
            mail.send(trigger.text)

            AppDB.dal.Triggers.remove(trigger.id)
          }
          Ok("thx")
        }) getOrElse BadRequest
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