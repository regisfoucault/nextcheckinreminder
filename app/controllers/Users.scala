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


case class UserData(
  prename: String,
  lastname: String,
  email: String,
  phone: String
)

object Users extends Controller with Secured {

  implicit val session = AppDB.database.withSession {
    implicit session: Session => session
  }

  def main() = IsAuthenticated { username => _ =>
    AppDB.dal.Users.get(username) map { user =>
      Async {
        WS.url("https://api.foursquare.com/v2/users/self?oauth_token=" + user.token + "&v=" + getDate).get().map { response =>
          (for {
            fUserId <- (response.json \ "response" \ "user" \ "id").asOpt[String]
            firstName <- (response.json \ "response" \ "user" \ "firstName").asOpt[String]
            lastName <- (response.json \ "response" \ "user" \ "lastName").asOpt[String]
            picPrefix <- (response.json \ "response" \ "user" \ "photo" \ "prefix").asOpt[String]
            picSuffix <- (response.json \ "response" \ "user" \ "photo" \ "suffix").asOpt[String]
            email <- (response.json \ "response" \ "user" \ "contact" \ "email").asOpt[String]
          } yield {
            Ok(html.app(fUserId, firstName, lastName, picPrefix + "110x110" + picSuffix, email, user.token, getDate))
          }) getOrElse Ok("problem parsing json dude !")
        }
      }
    } getOrElse NotFound
  }

  def loginPage() = Action {
    (for {
      clientId <- Play.current.configuration.getString("foursquare.client.id")
      redirectUri <- Play.current.configuration.getString("foursquare.client.redirectUri")
    } yield {
      Ok(html.login(clientId, redirectUri))
    }) getOrElse NotFound("clientId and/or redirectUri not found")
  }

  def getDate() = {
    val now = new DateTime()
    now.getYear() + digitToNumber(now.getMonthOfYear()) + digitToNumber(now.getDayOfMonth())
  }

  def digitToNumber(elt: Int): String = {
    if (elt < 10) {
      "0" + elt.toString
    } else {
      elt.toString
    }
  }

  def sendTrigger() = IsAuthenticated { username => implicit request =>
    triggerForm.bindFromRequest.fold(
      errors => {
        BadRequest(errors.toString())
      },
      myForm => {
        val trigger = new Trigger(
          id = UUID.randomUUID().toString(),
          fUId = username,
          vId = myForm._1,
          text = myForm._2
        )
        AppDB.dal.Triggers.saveNew(trigger)
        Ok("event saved")
      }
    )
  }

  def triggerForm = Form(
    tuple(
      "vid" -> text,
      "text" -> text
    )
  )

  def redirectCode(code: String) = Action {
    (for {
      clientId <- Play.current.configuration.getString("foursquare.client.id")
      clientSecret <- Play.current.configuration.getString("foursquare.client.secret")
      redirectUri <- Play.current.configuration.getString("foursquare.client.redirectUri")
    } yield {
      val feedUrl = "https://foursquare.com/oauth2/access_token?client_id=" + clientId + "&client_secret=" + clientSecret + "&grant_type=authorization_code&redirect_uri=" + redirectUri + "&code=" + code
      println(feedUrl)
      Async {
        WS.url(feedUrl).get().map { response =>
          val access_token = (response.json \ "access_token").as[String]
          Async {
            WS.url("https://api.foursquare.com/v2/users/self?oauth_token=" + access_token + "&v=" + getDate).get().map { response =>
              (for {
                fUserId <- (response.json \ "response" \ "user" \ "id").asOpt[String]
                /*firstName <- (response.json \ "response" \ "user" \ "firstName").asOpt[String]
                lastName <- (response.json \ "response" \ "user" \ "lastName").asOpt[String]
                picPrefix <- (response.json \ "response" \ "user" \ "photo" \ "prefix").asOpt[String]*/
              } yield {
                AppDB.dal.Users.storeToken(fUserId, access_token)
                Redirect(routes.Users.main).withSession(Security.username -> fUserId)
              }) getOrElse Ok("problem parsing json dude !")
            }
          }
        }
      }
    }) getOrElse BadRequest
  }
}