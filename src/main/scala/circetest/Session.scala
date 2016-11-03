package circetest

import java.net.URL
import java.time.ZonedDateTime
import java.util.UUID

object Session extends AggregateRoot {

  case class Id(sessionId: String) {
    override def toString: String = sessionId
  }

  sealed trait Event extends EventLike

  object Event {

    case class SessionInitialized(phoneNumber: PhoneNumber,
                                  successCallback: URL,
                                  errorCallback: URL,
                                  email: Email,
                                  personaId: PersonaId,
                                  klarnaDeviceId: KlarnaDeviceId,
                                  orderAccountId: OrderAccountId,
                                  orderId: OrderId,
                                  klarnaCorrelationId: KlarnaCorrelationId) extends Event with InitialEvent {

      val id = Id(UUID.randomUUID().toString)
      val timestamp = ZonedDateTime.now()

      override def create = State.SessionInitialized(
        id = id,
        phoneNumber = phoneNumber,
        successCallback = successCallback,
        errorCallback = errorCallback,
        email = email,
        personaId = personaId,
        klarnaDeviceId = klarnaDeviceId,
        orderAccountId = orderAccountId,
        orderId = orderId)

      override type TargetState = State.SessionInitialized
    }

    case class MethodInitialized(token: ConverseToken,
                                 timestamp: ZonedDateTime = ZonedDateTime.now()) extends Event with TransitionEvent {

      override def transition = {
        case f: State.MethodInitializationFailed => State.MethodInitialized(f.c, token)
        case c: State.SessionInitialized => State.MethodInitialized(c, token)
      }

      override type TargetState = State.MethodInitialized
    }

    case class MethodInitializationFailure(errorMessage: String,
                                           timestamp: ZonedDateTime = ZonedDateTime.now()) extends Event with TransitionEvent {
      override def transition = {
        case c: State.SessionInitialized => State.MethodInitializationFailed(c, 1)
        case State.MethodInitializationFailed(c, num) => State.MethodInitializationFailed(c, num + 1)
      }

      override type TargetState = State.MethodInitializationFailed
    }

    case class AttemptFailed(errorMessage: String,
                             timestamp: ZonedDateTime = ZonedDateTime.now()) extends Event with TransitionEvent {
      override def transition = {
        case cas: State.MethodInitialized => cas
      }

      override type TargetState = State.MethodInitialized
    }

    case class Succeeded(timestamp: ZonedDateTime = ZonedDateTime.now()) extends Event with TransitionEvent {
      override def transition = {
        case s: State.MethodInitialized => State.Succeeded(s)
      }

      override type TargetState = State.Succeeded
    }

    case class Failed(failType: FailType, timestamp: ZonedDateTime = ZonedDateTime.now()) extends Event with TransitionEvent {

      override def transition = {
        case failed: State.MethodInitialized => State.Failed(failed.id, failType)
      }

      override type TargetState = State.Failed
    }

  }

  trait State extends StateLike

  object State {

    case class SessionInitialized(id: Session.Id,
                                  phoneNumber: PhoneNumber,
                                  successCallback: URL,
                                  errorCallback: URL,
                                  email: Email,
                                  personaId: PersonaId,
                                  klarnaDeviceId: KlarnaDeviceId,
                                  orderAccountId: OrderAccountId,
                                  orderId: OrderId) extends State

    case class MethodInitialized(id: Session.Id,
                                 sessionInitialized: SessionInitialized,
                                 token: ConverseToken) extends State

    object MethodInitialized {
      def apply(c: SessionInitialized, token: ConverseToken): MethodInitialized = MethodInitialized(c.id, c, token)
    }

    case class MethodInitializationFailed(c: SessionInitialized, numberOfFailures: Int) extends State {
      override def id: Session.Id = c.id
    }

    case class Failed(id: Session.Id, failType: FailType) extends State

    case class Succeeded(s: MethodInitialized) extends State {
      override def id: Session.Id = s.id
    }

  }

}