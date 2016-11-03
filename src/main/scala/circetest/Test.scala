package circetest

object Test {

  def main(args: Array[String]): Unit = {
    import io.circe.generic.auto._
    import io.circe.syntax._

    val failType: FailType = FailType.PasswordExpired
    println(failType.asJson.toString)
  }
}