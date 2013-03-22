package models

import slick.driver.ExtendedProfile


class DAL(override val profile: ExtendedProfile) extends UserComponent with
  Profile with TriggerComponent {

  import profile.simple._

  def create(implicit session: Session): Unit = {
    //Triggers.ddl.create
    //Users.ddl.create
    //ReedSeanceQuizzes.ddl.create
  }
}
