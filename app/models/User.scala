package models

case class User(
  id: String,
  token: String,
  firstName: String,
  lastName: String,
  email: String
) 

trait UserComponent {
  this: Profile =>

  import profile.simple._

  object Users extends Table[User]("users") {
    def id = column[String]("id", O.PrimaryKey)
    def token = column[String]("token")
    def firstName = column[String]("firstname")
    def lastName = column[String]("lastname")
    def email = column[String]("email")
    def * = id ~ token ~ firstName ~ lastName ~ email <> (User, User.unapply _)

    def storeToken(id: String, firstName: String, lastName: String, email: String, token: String)(implicit session: Session) = {
      get(id) map { user =>
        save(user.copy(
          token = token,
          firstName = firstName,
          lastName = lastName,
          email = email
        ))
      } getOrElse {
        saveNew(id, token, firstName, lastName, email)
      }
    }

    def saveNew(id: String, token: String, firstName: String, lastName: String, email: String)(implicit session: Session) = {
      this.insert(new User(id, token, firstName, lastName, email))
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