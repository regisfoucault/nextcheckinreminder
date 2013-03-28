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

    def saveNew(user: User)(implicit session: Session) = {
      this.insert(user)
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