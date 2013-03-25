package models

case class Trigger(
  id: String,
  fUId: String,
  vId: String,
  text: String
)

trait TriggerComponent {
  this: Profile =>

  import profile.simple._

  object Triggers extends Table[Trigger]("triggers") {
    def id = column[String]("id", O.PrimaryKey)
    def fUId = column[String]("fuid")
    def vId = column[String]("vid")
    def text = column[String]("text")
    def * = id ~ fUId ~ vId ~ text <> (Trigger, Trigger.unapply _)

    def saveNew(trigger: Trigger)(implicit session: Session) = {
      this.insert(trigger)
    }

    def doLaunch(fUId: String, vid: String)(implicit session: Session) = {
      val q = for(u <- Triggers if (u.fUId === fUId && u.vId === vid)) yield u
      q.list()
    }

    /*def get(id: String)(implicit session: Session): Option[User] = {
      val q = for(u <- Users if u.id === id) yield u
      q.list().headOption
    }

    def save(user: User)(implicit session: Session) = {
      val q = for(u <- Users if u.id === user.id) yield u
      q.update(user)
    }*/

    def remove(id: String)(implicit session: Session) = {
      val q = for(u <- Triggers if u.id === id) yield u
      q.delete
    }
  }

}