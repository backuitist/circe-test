package circetest.session

sealed trait Event

object Event {

  case class Failed(reason: String) extends Event

}