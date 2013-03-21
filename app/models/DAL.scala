package models

import slick.driver.ExtendedProfile


class DAL(override val profile: ExtendedProfile) extends UserComponent with
  Profile {

  import profile.simple._

  def create(implicit session: Session): Unit = {
    //Users.ddl.create
    //ReedSeanceQuizzes.ddl.create
  }
}
