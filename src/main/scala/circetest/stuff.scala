package circetest

sealed trait FailType // (val message: String)

object FailType {

  case object PasswordExpired extends FailType // ("Password has expired")
  case object TooManyPasswordAttempts extends FailType//("Too many password attempts")
}