package circetest

case class Password(pwd: String) extends AnyVal

case class PasswordLength(len: Int) extends AnyVal

case class KlarnaCorrelationId private[KlarnaCorrelationId](id: String) extends {
  override def toString: String = id
}

sealed trait FailType

object FailType {

  case object PasswordExpired extends FailType
  case object TooManyPasswordAttempts extends FailType
  case class TechnicalIssue(tiType: TechnicalIssueType) extends FailType
}

sealed trait TechnicalIssueType

object TechnicalIssueType {
  case object ConverseUnrecognizedSession extends TechnicalIssueType
  case object OtherTechnicalIssue extends TechnicalIssueType
}


// TODO Enforce: Phone number starting with "+" in international format. All non-numerics will be removed on the server side [in ConVerSe].
case class PhoneNumber(num: String) extends AnyVal

case class ConverseToken(content: String) extends AnyVal {
  override def toString: String = content
}

case class PersonaId(id: String) extends AnyVal

case class KlarnaDeviceId(id: String) extends AnyVal

case class OrderAccountId(id: String) extends AnyVal

case class OrderId(id: String) extends AnyVal

case class Email(email: String) extends AnyVal

