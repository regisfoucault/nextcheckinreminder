package models

case class User(
  id: String,
  token: String
) 

trait UserComponent {
  this: Profile =>

  import profile.simple._

  object Users extends Table[User]("users") {
    def id = column[String]("id", O.PrimaryKey)
    def token = column[String]("token")
    def * = id ~ token <> (User, User.unapply _)

    def storeToken(id: String, token: String)(implicit session: Session) = {
      get(id) map { user =>
        save(user.copy(token = token))
      } getOrElse {
        saveNew(id, token)
      }
    }

    def saveNew(id: String, token: String)(implicit session: Session) = {
      this.insert(new User(id, token))
    }

    def get(id: String)(implicit session: Session): Option[User] = {
      val q = for(u <- Users if u.id === id) yield u
      q.list().headOption
    }

    def save(user: User)(implicit session: Session) = {
      val q = for(u <- Users if u.id === user.id) yield u
      q.update(user)
    }

    /*def remove(uid: String)(implicit session: Session) = {
      val q = for(u <- Users if u.id === uid) yield u
      q.delete
    }*/
  }

}