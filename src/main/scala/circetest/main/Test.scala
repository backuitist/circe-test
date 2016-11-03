package circetest.main

import java.net.URL

import circetest.{FailType, Session}
import io.circe.{Decoder, Encoder}

object Test {

  def main(args: Array[String]): Unit = {
    import io.circe.generic.auto._
    import io.circe.syntax._

    implicit val urlEncoder: Encoder[URL] = Encoder[String].contramap[URL](_.toString)
    implicit val urlDecoder: Decoder[URL] = Decoder[String].map[URL](new URL(_))

    val event: Session.Event = Session.Event.Failed(FailType.PasswordExpired)
    println(event.asJson.toString)
  }
}