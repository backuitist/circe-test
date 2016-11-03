package circetest.main

import circetest.session.Event

object Test {

  def main(args: Array[String]): Unit = {
    import io.circe.generic.auto._
    import io.circe.syntax._

    val event: Event = Event.Failed("x")
    println(event.asJson.toString)
  }
}